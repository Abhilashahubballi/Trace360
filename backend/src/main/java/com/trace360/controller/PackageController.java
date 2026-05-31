package com.trace360.controller;

import com.trace360.dto.request.CreatePackageRequest;
import com.trace360.dto.response.*;
import com.trace360.entity.Package.DeliveryStatus;
import com.trace360.service.PackageService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class PackageController {

    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @GetMapping("/api/packages/track/{trackingId}")
    public ResponseEntity<ApiResponse<PackageResponse>> trackPackage(@PathVariable String trackingId) {
        return ResponseEntity.ok(ApiResponse.success("Package found", packageService.trackPackage(trackingId)));
    }

    @PostMapping("/api/admin/packages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PackageResponse>> createPackage(@Valid @RequestBody CreatePackageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Package created successfully", packageService.createPackage(request)));
    }

    @GetMapping("/api/admin/packages")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PackageResponse>>> getAllPackages() {
        return ResponseEntity.ok(ApiResponse.success("All packages", packageService.getAllPackages()));
    }

    @GetMapping("/api/admin/packages/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PackageResponse>>> getByStatus(@PathVariable DeliveryStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Packages by status", packageService.getPackagesByStatus(status)));
    }

    @DeleteMapping("/api/admin/packages/{trackingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePackage(@PathVariable String trackingId) {
        packageService.deletePackage(trackingId);
        return ResponseEntity.ok(ApiResponse.success("Package deleted", null));
    }

    @GetMapping("/api/agent/packages")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<ApiResponse<List<PackageResponse>>> getAgentPackages(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Agent packages",
            packageService.getAgentPackages(userDetails.getUsername())));
    }
}
