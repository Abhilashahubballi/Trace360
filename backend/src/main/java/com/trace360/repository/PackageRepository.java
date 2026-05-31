package com.trace360.repository;

import com.trace360.entity.Package;
import com.trace360.entity.Package.DeliveryStatus;
import com.trace360.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PackageRepository extends JpaRepository<Package, Long> {
    Optional<Package> findByTrackingId(String trackingId);
    boolean existsByTrackingId(String trackingId);
    List<Package> findByStatus(DeliveryStatus status);
    List<Package> findByAssignedAgent(User agent);
    long countByStatus(DeliveryStatus status);

    @Query("SELECT p FROM Package p LEFT JOIN FETCH p.assignedAgent WHERE p.trackingId = :tid")
    Optional<Package> findByTrackingIdWithAgent(@Param("tid") String trackingId);
}
