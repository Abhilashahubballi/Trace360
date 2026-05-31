package com.trace360.controller;

import com.trace360.dto.request.LocationUpdateRequest;
import com.trace360.dto.response.*;
import com.trace360.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping("/api/location/update")
    @PreAuthorize("hasRole('AGENT')")
    public ResponseEntity<ApiResponse<LocationResponse>> updateLocation(
            @Valid @RequestBody LocationUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Location updated",
            locationService.updateLocation(request, userDetails.getUsername())));
    }

    @GetMapping("/api/packages/track/{trackingId}/location")
    public ResponseEntity<ApiResponse<LocationResponse>> getLatestLocation(@PathVariable String trackingId) {
        return ResponseEntity.ok(ApiResponse.success("Latest location",
            locationService.getLatestLocation(trackingId)));
    }

    @GetMapping("/api/admin/packages/{trackingId}/route")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getRouteHistory(@PathVariable String trackingId) {
        return ResponseEntity.ok(ApiResponse.success("Route history",
            locationService.getRouteHistory(trackingId)));
    }
}
