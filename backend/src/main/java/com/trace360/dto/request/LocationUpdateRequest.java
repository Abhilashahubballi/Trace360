package com.trace360.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LocationUpdateRequest {
    @NotBlank
    private String trackingId;
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;

    public LocationUpdateRequest() {}
    public String getTrackingId() { return trackingId; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public void setTrackingId(String v) { this.trackingId = v; }
    public void setLatitude(Double v) { this.latitude = v; }
    public void setLongitude(Double v) { this.longitude = v; }
}
