package com.trace360.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otp_records")
public class OtpRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "otp_value", nullable = false, length = 6)
    private String otpValue;

    @Column(name = "request_count", nullable = false)
    private int requestCount = 1;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "verified", nullable = false)
    private boolean verified = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public OtpRecord() {}

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getOtpValue() { return otpValue; }
    public int getRequestCount() { return requestCount; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public boolean isVerified() { return verified; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setOtpValue(String otpValue) { this.otpValue = otpValue; }
    public void setRequestCount(int requestCount) { this.requestCount = requestCount; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final OtpRecord o = new OtpRecord();
        public Builder email(String v) { o.email = v; return this; }
        public Builder otpValue(String v) { o.otpValue = v; return this; }
        public Builder requestCount(int v) { o.requestCount = v; return this; }
        public Builder expiresAt(LocalDateTime v) { o.expiresAt = v; return this; }
        public Builder verified(boolean v) { o.verified = v; return this; }
        public OtpRecord build() { return o; }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
