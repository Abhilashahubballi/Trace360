package com.trace360.service;

import com.trace360.dto.request.CreatePackageRequest;
import com.trace360.dto.response.*;
import com.trace360.entity.Package;
import com.trace360.entity.Package.DeliveryStatus;
import com.trace360.entity.User;
import com.trace360.exception.*;
import com.trace360.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class PackageService {

    public PackageService(PackageRepository packageRepository, UserRepository userRepository, LocationRepository locationRepository, EmailService emailService) {
        this.packageRepository = packageRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.emailService = emailService;
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PackageService.class);

    private final PackageRepository packageRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final EmailService emailService;

    public PackageResponse trackPackage(String trackingId) {
        Package pkg = packageRepository.findByTrackingIdWithAgent(trackingId)
            .orElseThrow(() -> new ResourceNotFoundException("No package found: " + trackingId));
        Double lat = null, lng = null;
        var loc = locationRepository.findLatestByPackage(pkg);
        if (loc.isPresent()) { lat = loc.get().getLatitude(); lng = loc.get().getLongitude(); }
        return mapToResponse(pkg, lat, lng);
    }

    @Transactional
    public PackageResponse createPackage(CreatePackageRequest request) {
        User agent = null;
        if (request.getAgentId() != null) {
            agent = userRepository.findById(request.getAgentId())
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found: " + request.getAgentId()));
            if (agent.getRole() != User.Role.AGENT)
                throw new ValidationException("Assigned user is not an agent");
        }

        String trackingId = generateTrackingId();

        Package pkg = Package.builder()
            .trackingId(trackingId)
            .senderName(request.getSenderName())
            .senderCity(request.getSenderCity())
            .receiverName(request.getReceiverName())
            .receiverEmail(request.getReceiverEmail())
            .destination(request.getDestination())
            .weightKg(request.getWeightKg())
            .packageType(request.getPackageType())
            .priority(request.getPriority())
            .status(DeliveryStatus.PENDING)
            .assignedAgent(agent)
            .eta("1-2 business days")
            .build();

        Package saved = packageRepository.save(pkg);
        log.info("Package created: {} for {}", trackingId, request.getReceiverEmail());

        if (request.isSendEmailToReceiver()) {
            try {
                emailService.sendTrackingEmail(request.getReceiverEmail(), request.getReceiverName(), trackingId);
            } catch (Exception e) {
                log.warn("Tracking email failed: {}", e.getMessage());
            }
        }
        return mapToResponse(saved, null, null);
    }

    public List<PackageResponse> getAllPackages() {
        return packageRepository.findAll().stream()
            .map(p -> mapToResponse(p, null, null)).collect(Collectors.toList());
    }

    public List<PackageResponse> getPackagesByStatus(DeliveryStatus status) {
        return packageRepository.findByStatus(status).stream()
            .map(p -> mapToResponse(p, null, null)).collect(Collectors.toList());
    }

    public List<PackageResponse> getAgentPackages(String agentEmail) {
        User agent = userRepository.findByEmail(agentEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));
        return packageRepository.findByAssignedAgent(agent).stream()
            .map(p -> mapToResponse(p, null, null)).collect(Collectors.toList());
    }

    @Transactional
    public PackageResponse updateStatus(String trackingId, DeliveryStatus newStatus) {
        Package pkg = packageRepository.findByTrackingId(trackingId)
            .orElseThrow(() -> new ResourceNotFoundException("Package not found: " + trackingId));
        pkg.setStatus(newStatus);
        Package updated = packageRepository.save(pkg);
        try {
            emailService.sendStatusUpdateEmail(updated.getReceiverEmail(),
                updated.getReceiverName(), trackingId, newStatus.name());
        } catch (Exception e) {
            log.warn("Status email failed: {}", e.getMessage());
        }
        return mapToResponse(updated, null, null);
    }

    @Transactional
    public void deletePackage(String trackingId) {
        Package pkg = packageRepository.findByTrackingId(trackingId)
            .orElseThrow(() -> new ResourceNotFoundException("Package not found: " + trackingId));
        packageRepository.delete(pkg);
        log.info("Package deleted: {}", trackingId);
    }

    private String generateTrackingId() {
        String date = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String id;
        int attempts = 0;
        do {
            String seq = String.format("%03d", new Random().nextInt(900) + 100);
            id = "TRK-" + date + "-" + seq;
            if (++attempts > 100) throw new RuntimeException("Could not generate unique tracking ID");
        } while (packageRepository.existsByTrackingId(id));
        return id;
    }

    private PackageResponse mapToResponse(Package pkg, Double lat, Double lng) {
        String agentName = null, agentIdCode = null;
        if (pkg.getAssignedAgent() != null) {
            agentName = pkg.getAssignedAgent().getFullName();
            agentIdCode = "AG-" + String.format("%03d", pkg.getAssignedAgent().getId());
        }
        String statusLabel = switch (pkg.getStatus()) {
            case PENDING -> "Pickup Scheduled";
            case PICKED_UP -> "Picked Up";
            case IN_TRANSIT -> "In Transit";
            case OUT_FOR_DELIVERY -> "Out for Delivery";
            case DELIVERED -> "Delivered";
            case FAILED -> "Delivery Failed";
        };
        return PackageResponse.builder()
            .id(pkg.getId()).trackingId(pkg.getTrackingId())
            .senderName(pkg.getSenderName()).senderCity(pkg.getSenderCity())
            .receiverName(pkg.getReceiverName()).receiverEmail(pkg.getReceiverEmail())
            .destination(pkg.getDestination()).weightKg(pkg.getWeightKg())
            .packageType(pkg.getPackageType()).priority(pkg.getPriority())
            .status(pkg.getStatus()).statusLabel(statusLabel)
            .agentName(agentName).agentId(agentIdCode)
            .eta(pkg.getEta()).createdAt(pkg.getCreatedAt())
            .deliveredAt(pkg.getDeliveredAt())
            .currentLatitude(lat).currentLongitude(lng)
            .build();
    }
}
