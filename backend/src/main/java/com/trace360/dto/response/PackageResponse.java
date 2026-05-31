package com.trace360.dto.response;

import java.time.LocalDateTime;

public class PackageResponse {
    private Long id;
    private String trackingId;
    private String senderName;
    private String senderCity;
    private String receiverName;
    private String receiverEmail;
    private String destination;
    private Double weightKg;
    private String packageType;
    private String priority;
    private String status;
    private String statusLabel;
    private String eta;
    private String agentName;
    private String agentId;
    private Double currentLatitude;
    private Double currentLongitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deliveredAt;

    public PackageResponse() {}

    public Long getId() { return id; }
    public String getTrackingId() { return trackingId; }
    public String getSenderName() { return senderName; }
    public String getSenderCity() { return senderCity; }
    public String getReceiverName() { return receiverName; }
    public String getReceiverEmail() { return receiverEmail; }
    public String getDestination() { return destination; }
    public Double getWeightKg() { return weightKg; }
    public String getPackageType() { return packageType; }
    public String getPriority() { return priority; }
    public String getStatus() { return status; }
    public String getStatusLabel() { return statusLabel; }
    public String getEta() { return eta; }
    public String getAgentName() { return agentName; }
    public String getAgentId() { return agentId; }
    public Double getCurrentLatitude() { return currentLatitude; }
    public Double getCurrentLongitude() { return currentLongitude; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }

    public void setId(Long v) { this.id = v; }
    public void setTrackingId(String v) { this.trackingId = v; }
    public void setSenderName(String v) { this.senderName = v; }
    public void setSenderCity(String v) { this.senderCity = v; }
    public void setReceiverName(String v) { this.receiverName = v; }
    public void setReceiverEmail(String v) { this.receiverEmail = v; }
    public void setDestination(String v) { this.destination = v; }
    public void setWeightKg(Double v) { this.weightKg = v; }
    public void setPackageType(String v) { this.packageType = v; }
    public void setPriority(String v) { this.priority = v; }
    public void setStatus(String v) { this.status = v; }
    public void setStatusLabel(String v) { this.statusLabel = v; }
    public void setEta(String v) { this.eta = v; }
    public void setAgentName(String v) { this.agentName = v; }
    public void setAgentId(String v) { this.agentId = v; }
    public void setCurrentLatitude(Double v) { this.currentLatitude = v; }
    public void setCurrentLongitude(Double v) { this.currentLongitude = v; }
    public void setCreatedAt(LocalDateTime v) { this.createdAt = v; }
    public void setUpdatedAt(LocalDateTime v) { this.updatedAt = v; }
    public void setDeliveredAt(LocalDateTime v) { this.deliveredAt = v; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final PackageResponse r = new PackageResponse();
        public Builder id(Long v) { r.id = v; return this; }
        public Builder trackingId(String v) { r.trackingId = v; return this; }
        public Builder senderName(String v) { r.senderName = v; return this; }
        public Builder senderCity(String v) { r.senderCity = v; return this; }
        public Builder receiverName(String v) { r.receiverName = v; return this; }
        public Builder receiverEmail(String v) { r.receiverEmail = v; return this; }
        public Builder destination(String v) { r.destination = v; return this; }
        public Builder weightKg(Double v) { r.weightKg = v; return this; }
        public Builder packageType(String v) { r.packageType = v; return this; }
        public Builder priority(String v) { r.priority = v; return this; }
        public Builder status(Object v) { r.status = v != null ? v.toString() : null; return this; }
        public Builder statusLabel(String v) { r.statusLabel = v; return this; }
        public Builder eta(String v) { r.eta = v; return this; }
        public Builder agentName(String v) { r.agentName = v; return this; }
        public Builder agentId(String v) { r.agentId = v; return this; }
        public Builder currentLatitude(Double v) { r.currentLatitude = v; return this; }
        public Builder currentLongitude(Double v) { r.currentLongitude = v; return this; }
        public Builder createdAt(java.time.LocalDateTime v) { r.createdAt = v; return this; }
        public Builder updatedAt(java.time.LocalDateTime v) { r.updatedAt = v; return this; }
        public Builder deliveredAt(java.time.LocalDateTime v) { r.deliveredAt = v; return this; }
        public PackageResponse build() { return r; }
    }
}
