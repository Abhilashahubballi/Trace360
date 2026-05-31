package com.trace360.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_confirmations")
public class DeliveryConfirmation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false, unique = true)
    private Package aPackage;

    @Column(name = "delivery_otp", length = 4)
    private String deliveryOtp;

    @Column(name = "otp_verified", nullable = false)
    private boolean otpVerified = false;

    @Column(name = "photo_proof_url", length = 500)
    private String photoProofUrl;

    @Column(name = "signature", columnDefinition = "TEXT")
    private String signature;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private User confirmedByAgent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public DeliveryConfirmation() {}

    public Long getId() { return id; }
    public Package getAPackage() { return aPackage; }
    public String getDeliveryOtp() { return deliveryOtp; }
    public boolean isOtpVerified() { return otpVerified; }
    public String getPhotoProofUrl() { return photoProofUrl; }
    public String getSignature() { return signature; }
    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public User getConfirmedByAgent() { return confirmedByAgent; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setAPackage(Package aPackage) { this.aPackage = aPackage; }
    public void setDeliveryOtp(String deliveryOtp) { this.deliveryOtp = deliveryOtp; }
    public void setOtpVerified(boolean otpVerified) { this.otpVerified = otpVerified; }
    public void setPhotoProofUrl(String photoProofUrl) { this.photoProofUrl = photoProofUrl; }
    public void setSignature(String signature) { this.signature = signature; }
    public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }
    public void setConfirmedByAgent(User confirmedByAgent) { this.confirmedByAgent = confirmedByAgent; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final DeliveryConfirmation dc = new DeliveryConfirmation();
        public Builder aPackage(Package v) { dc.aPackage = v; return this; }
        public Builder deliveryOtp(String v) { dc.deliveryOtp = v; return this; }
        public Builder otpVerified(boolean v) { dc.otpVerified = v; return this; }
        public Builder confirmedByAgent(User v) { dc.confirmedByAgent = v; return this; }
        public Builder confirmedAt(LocalDateTime v) { dc.confirmedAt = v; return this; }
        public DeliveryConfirmation build() { return dc; }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public boolean isOtpExpired() {
        return LocalDateTime.now().isAfter(createdAt.plusMinutes(30));
    }
}
