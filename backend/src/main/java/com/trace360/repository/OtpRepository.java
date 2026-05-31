package com.trace360.repository;

import com.trace360.entity.OtpRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpRecord, Long> {

    // Get the latest OTP record for an email
    @Query("SELECT o FROM OtpRecord o WHERE o.email = :email ORDER BY o.createdAt DESC LIMIT 1")
    Optional<OtpRecord> findLatestByEmail(@Param("email") String email);

    // Count total OTP requests for this email today
    @Query("SELECT COUNT(o) FROM OtpRecord o WHERE o.email = :email AND o.createdAt >= CURRENT_DATE")
    long countTodayRequestsByEmail(@Param("email") String email);

    // Delete all OTP records for an email (after successful registration)
    @Modifying
    @Query("DELETE FROM OtpRecord o WHERE o.email = :email")
    void deleteAllByEmail(@Param("email") String email);
}
