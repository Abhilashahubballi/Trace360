package com.trace360.controller;

import com.trace360.dto.response.ApiResponse;
import com.trace360.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agent")
@PreAuthorize("hasRole('AGENT')")
public class AgentController {

    private final LocationService locationService;

    public AgentController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyDeliveryOtp(
            @RequestParam String trackingId,
            @RequestParam String otp,
            @AuthenticationPrincipal UserDetails userDetails) {
        boolean verified = locationService.verifyDeliveryOtp(trackingId, otp, userDetails.getUsername());
        if (verified) {
            return ResponseEntity.ok(ApiResponse.success("Delivery confirmed successfully!", "DELIVERED"));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.error("Invalid OTP. Please try again."));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Agent API is running", "OK"));
    }
}
