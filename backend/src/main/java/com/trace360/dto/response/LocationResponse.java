package com.trace360.dto.response;

import java.time.LocalDateTime;

public class LocationResponse {
    private Long id;
    private String trackingId;
    private Double latitude;
    private Double longitude;
    private String status;
    private String statusAtTime;
    private String eta;
    private LocalDateTime recordedAt;

    public LocationResponse() {}

    public Long getId() { return id; }
    public String getTrackingId() { return trackingId; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getStatus() { return status; }
    public String getStatusAtTime() { return statusAtTime; }
    public String getEta() { return eta; }
    public LocalDateTime getRecordedAt() { return recordedAt; }

    public void setId(Long v) { this.id = v; }
    public void setTrackingId(String v) { this.trackingId = v; }
    public void setLatitude(Double v) { this.latitude = v; }
    public void setLongitude(Double v) { this.longitude = v; }
    public void setStatus(String v) { this.status = v; }
    public void setStatusAtTime(String v) { this.statusAtTime = v; }
    public void setEta(String v) { this.eta = v; }
    public void setRecordedAt(LocalDateTime v) { this.recordedAt = v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final LocationResponse r = new LocationResponse();
        public Builder id(Long v) { r.id = v; return this; }
        public Builder trackingId(String v) { r.trackingId = v; return this; }
        public Builder latitude(Double v) { r.latitude = v; return this; }
        public Builder longitude(Double v) { r.longitude = v; return this; }
        public Builder status(String v) { r.status = v; return this; }
        public Builder statusAtTime(String v) { r.statusAtTime = v; return this; }
        public Builder eta(String v) { r.eta = v; return this; }
        public Builder recordedAt(LocalDateTime v) { r.recordedAt = v; return this; }
        public LocationResponse build() { return r; }
    }
}
