package com.trace360.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "packages")
public class Package {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tracking_id", nullable = false, unique = true, length = 25)
    private String trackingId;

    @Column(name = "sender_name", nullable = false, length = 100)
    private String senderName;

    @Column(name = "sender_city", nullable = false, length = 100)
    private String senderCity;

    @Column(name = "receiver_name", nullable = false, length = 100)
    private String receiverName;

    @Column(name = "receiver_email", nullable = false, length = 150)
    private String receiverEmail;

    @Column(name = "destination", nullable = false, length = 200)
    private String destination;

    @Column(name = "weight_kg", nullable = false)
    private Double weightKg;

    @Column(name = "package_type", length = 50)
    private String packageType = "Parcel";

    @Column(name = "priority", length = 20)
    private String priority = "Standard";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeliveryStatus status = DeliveryStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private User assignedAgent;

    @Column(name = "eta", length = 100)
    private String eta;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    public Package() {}

    // ── Getters ──────────────────────────────────
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
    public DeliveryStatus getStatus() { return status; }
    public User getAssignedAgent() { return assignedAgent; }
    public String getEta() { return eta; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }

    // ── Setters ──────────────────────────────────
    public void setId(Long id) { this.id = id; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public void setSenderCity(String senderCity) { this.senderCity = senderCity; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public void setReceiverEmail(String receiverEmail) { this.receiverEmail = receiverEmail; }
    public void setDestination(String destination) { this.destination = destination; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }
    public void setPackageType(String packageType) { this.packageType = packageType; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setStatus(DeliveryStatus status) { this.status = status; }
    public void setAssignedAgent(User assignedAgent) { this.assignedAgent = assignedAgent; }
    public void setEta(String eta) { this.eta = eta; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }

    // ── Builder ───────────────────────────────────
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Package pkg = new Package();
        public Builder trackingId(String v) { pkg.trackingId = v; return this; }
        public Builder senderName(String v) { pkg.senderName = v; return this; }
        public Builder senderCity(String v) { pkg.senderCity = v; return this; }
        public Builder receiverName(String v) { pkg.receiverName = v; return this; }
        public Builder receiverEmail(String v) { pkg.receiverEmail = v; return this; }
        public Builder destination(String v) { pkg.destination = v; return this; }
        public Builder weightKg(Double v) { pkg.weightKg = v; return this; }
        public Builder packageType(String v) { pkg.packageType = v; return this; }
        public Builder priority(String v) { pkg.priority = v; return this; }
        public Builder status(DeliveryStatus v) { pkg.status = v; return this; }
        public Builder assignedAgent(User v) { pkg.assignedAgent = v; return this; }
        public Builder eta(String v) { pkg.eta = v; return this; }
        public Package build() { return pkg; }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (status == DeliveryStatus.DELIVERED && deliveredAt == null) {
            deliveredAt = LocalDateTime.now();
        }
    }

    public enum DeliveryStatus {
        PENDING, PICKED_UP, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, FAILED
    }
}
