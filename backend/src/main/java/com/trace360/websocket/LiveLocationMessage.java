package com.trace360.websocket;

import com.trace360.entity.Package.DeliveryStatus;
import java.time.LocalDateTime;

public class LiveLocationMessage {
    private String trackingId;
    private Double latitude;
    private Double longitude;
    private DeliveryStatus status;
    private String statusLabel;
    private String agentName;
    private Double distanceToDestinationKm;
    private String estimatedArrival;
    private LocalDateTime timestamp;

    public LiveLocationMessage() {}

    public String getTrackingId() { return trackingId; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public DeliveryStatus getStatus() { return status; }
    public String getStatusLabel() { return statusLabel; }
    public String getAgentName() { return agentName; }
    public Double getDistanceToDestinationKm() { return distanceToDestinationKm; }
    public String getEstimatedArrival() { return estimatedArrival; }
    public LocalDateTime getTimestamp() { return timestamp; }

    public void setTrackingId(String v) { this.trackingId = v; }
    public void setLatitude(Double v) { this.latitude = v; }
    public void setLongitude(Double v) { this.longitude = v; }
    public void setStatus(DeliveryStatus v) { this.status = v; }
    public void setStatusLabel(String v) { this.statusLabel = v; }
    public void setAgentName(String v) { this.agentName = v; }
    public void setDistanceToDestinationKm(Double v) { this.distanceToDestinationKm = v; }
    public void setEstimatedArrival(String v) { this.estimatedArrival = v; }
    public void setTimestamp(LocalDateTime v) { this.timestamp = v; }

    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private final LiveLocationMessage m = new LiveLocationMessage();
        public Builder trackingId(String v) { m.trackingId = v; return this; }
        public Builder latitude(Double v) { m.latitude = v; return this; }
        public Builder longitude(Double v) { m.longitude = v; return this; }
        public Builder status(DeliveryStatus v) { m.status = v; return this; }
        public Builder statusLabel(String v) { m.statusLabel = v; return this; }
        public Builder agentName(String v) { m.agentName = v; return this; }
        public Builder distanceToDestinationKm(Double v) { m.distanceToDestinationKm = v; return this; }
        public Builder estimatedArrival(String v) { m.estimatedArrival = v; return this; }
        public Builder timestamp(LocalDateTime v) { m.timestamp = v; return this; }
        public LiveLocationMessage build() { return m; }
    }
}
