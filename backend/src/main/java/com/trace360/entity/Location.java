package com.trace360.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "locations", indexes = {
    @Index(name = "idx_location_package", columnList = "package_id"),
    @Index(name = "idx_location_timestamp", columnList = "recorded_at")
})
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id", nullable = false)
    private Package aPackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private User agent;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_at_time")
    private Package.DeliveryStatus statusAtTime;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;

    public Location() {}

    public Long getId() { return id; }
    public Package getAPackage() { return aPackage; }
    public User getAgent() { return agent; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public Package.DeliveryStatus getStatusAtTime() { return statusAtTime; }
    public LocalDateTime getRecordedAt() { return recordedAt; }

    public void setId(Long id) { this.id = id; }
    public void setAPackage(Package aPackage) { this.aPackage = aPackage; }
    public void setAgent(User agent) { this.agent = agent; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public void setStatusAtTime(Package.DeliveryStatus statusAtTime) { this.statusAtTime = statusAtTime; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final Location loc = new Location();
        public Builder aPackage(Package v) { loc.aPackage = v; return this; }
        public Builder agent(User v) { loc.agent = v; return this; }
        public Builder latitude(Double v) { loc.latitude = v; return this; }
        public Builder longitude(Double v) { loc.longitude = v; return this; }
        public Builder statusAtTime(Package.DeliveryStatus v) { loc.statusAtTime = v; return this; }
        public Location build() { return loc; }
    }

    @PrePersist
    protected void onCreate() {
        recordedAt = LocalDateTime.now();
    }
}
