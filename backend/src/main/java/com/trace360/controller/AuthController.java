package com.trace360.controller;

import com.trace360.dto.request.*;
import com.trace360.dto.response.*;
import com.trace360.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse<OtpResponse>> sendOtp(@Valid @RequestBody OtpRequest request) {
        OtpResponse result = authService.sendOtp(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success("OTP sent successfully", result));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Trace360 API is running!");
    }
}
