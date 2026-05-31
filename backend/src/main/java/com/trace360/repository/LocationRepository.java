package com.trace360.repository;

import com.trace360.entity.Location;
import com.trace360.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query("SELECT l FROM Location l WHERE l.aPackage = :pkg ORDER BY l.recordedAt ASC")
    List<Location> findByAPackageOrderByRecordedAtAsc(@Param("pkg") Package pkg);

    @Query("SELECT l FROM Location l WHERE l.aPackage = :pkg ORDER BY l.recordedAt DESC")
    List<Location> findByAPackageOrderByRecordedAtDesc(@Param("pkg") Package pkg);

    @Query("SELECT l FROM Location l WHERE l.aPackage = :pkg ORDER BY l.recordedAt DESC")
    Optional<Location> findLatestByPackage(@Param("pkg") Package pkg);

    @Query("SELECT l FROM Location l WHERE l.aPackage.trackingId = :trackingId ORDER BY l.recordedAt DESC")
    List<Location> findByTrackingIdOrderByRecordedAtDesc(@Param("trackingId") String trackingId);
}
