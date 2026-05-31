package com.trace360.service;

import com.trace360.dto.request.*;
import com.trace360.dto.response.*;
import com.trace360.entity.OtpRecord;
import com.trace360.entity.User;
import com.trace360.exception.*;
import com.trace360.repository.*;
import com.trace360.security.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {

    public AuthService(UserRepository userRepository, OtpRepository otpRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthenticationManager authenticationManager, EmailService emailService, CustomUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.userDetailsService = userDetailsService;
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final CustomUserDetailsService userDetailsService;

    // OTP limit per email per day = 2
    private static final int MAX_OTP_ATTEMPTS = 2;
    // OTP valid for 10 minutes
    private static final int OTP_EXPIRY_MINUTES = 10;

    // ──────────────────────────────────────
    //  SEND OTP — max 2 times per email
    // ──────────────────────────────────────
    @Transactional
    public OtpResponse sendOtp(String email) {

        // 1. Check email not already registered
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("This email is already registered. Please sign in.");
        }

        // 2. Count how many OTPs already sent today for this email
        long totalRequestsToday = otpRepository.countTodayRequestsByEmail(email);

        // 3. Enforce 2-attempt limit
        if (totalRequestsToday >= MAX_OTP_ATTEMPTS) {
            throw new OtpLimitExceededException(
                "OTP limit reached. You can only request OTP 2 times. " +
                "Please try again tomorrow or contact support."
            );
        }

        // 4. Generate new 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));

        // 5. Save OTP record to database
        OtpRecord record = OtpRecord.builder()
            .email(email)
            .otpValue(otp)
            .requestCount((int) totalRequestsToday + 1)
            .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
            .verified(false)
            .build();
        otpRepository.save(record);

        // 6. Calculate remaining attempts
        int attemptsUsed = (int) totalRequestsToday + 1;
        int attemptsRemaining = MAX_OTP_ATTEMPTS - attemptsUsed;

        // 7. Send OTP email (async — non-blocking)
        try {
            emailService.sendOtpEmail(email, otp, attemptsRemaining);
        } catch (Exception e) {
            log.error("OTP email failed for {}: {}", email, e.getMessage());
            throw new RuntimeException("Failed to send OTP email. Please try again.");
        }

        log.info("OTP sent to {} (attempt {}/{})", email, attemptsUsed, MAX_OTP_ATTEMPTS);

        return OtpResponse.builder()
            .message("OTP sent to " + email)
            .attemptsUsed(attemptsUsed)
            .attemptsRemaining(attemptsRemaining)
            .limitReached(attemptsRemaining == 0)
            .build();
    }

    // ──────────────────────────────────────
    //  REGISTER
    // ──────────────────────────────────────
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        // 1. Check email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        // 2. Check phone uniqueness
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateResourceException("Phone number already registered.");
        }

        // 3. Validate phone — must not start with 0 or 123456
        validatePhone(request.getPhone());

        // 4. Fetch latest OTP record for this email
        OtpRecord otpRecord = otpRepository.findLatestByEmail(request.getEmail())
            .orElseThrow(() -> new InvalidOtpException(
                "No OTP found. Please request an OTP first."
            ));

        // 5. Check OTP not expired
        if (otpRecord.isExpired()) {
            throw new InvalidOtpException("OTP has expired. Please request a new one.");
        }

        // 6. Check OTP value matches
        if (!otpRecord.getOtpValue().equals(request.getOtp())) {
            throw new InvalidOtpException("Incorrect OTP. Please check and try again.");
        }

        // 7. Hash password
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // 8. Create and save user
        User user = User.builder()
            .fullName(request.getFullName())
            .email(request.getEmail())
            .password(hashedPassword)
            .phone(request.getPhone())
            .role(request.getRole())
            .emailVerified(true)
            .active(true)
            .build();

        User savedUser = userRepository.save(user);

        // 9. Clean up OTP records after successful registration
        otpRepository.deleteAllByEmail(request.getEmail());

        // 10. Generate JWT token
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        String token = jwtUtil.generateToken(userDetails, savedUser.getRole().name());

        log.info("User registered: {} ({})", savedUser.getEmail(), savedUser.getRole());

        // 11. Send welcome email
        try {
            emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFullName());
        } catch (Exception e) {
            log.warn("Welcome email failed: {}", e.getMessage());
        }

        return AuthResponse.builder()
            .token(token)
            .tokenType("Bearer")
            .userId(savedUser.getId())
            .fullName(savedUser.getFullName())
            .email(savedUser.getEmail())
            .role(savedUser.getRole())
            .emailVerified(true)
            .message("Account created successfully!")
            .build();
    }

    // ──────────────────────────────────────
    //  LOGIN
    // ──────────────────────────────────────
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(), request.getPassword()
                )
            );
        } catch (BadCredentialsException e) {
            throw new InvalidCredentialsException("Wrong email or password.");
        } catch (DisabledException e) {
            throw new AccountDisabledException("Your account has been deactivated.");
        }

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtUtil.generateToken(userDetails, user.getRole().name());

        log.info("User logged in: {} ({})", user.getEmail(), user.getRole());

        return AuthResponse.builder()
            .token(token)
            .tokenType("Bearer")
            .userId(user.getId())
            .fullName(user.getFullName())
            .email(user.getEmail())
            .role(user.getRole())
            .emailVerified(user.isEmailVerified())
            .message("Login successful!")
            .build();
    }

    // ──────────────────────────────────────
    //  PHONE VALIDATION
    // ──────────────────────────────────────
    private void validatePhone(String phone) {
        if (phone.startsWith("0")) {
            throw new ValidationException("Phone number cannot start with 0");
        }
        if (phone.startsWith("123456")) {
            throw new ValidationException("Phone number cannot start with 123456");
        }
    }
}
