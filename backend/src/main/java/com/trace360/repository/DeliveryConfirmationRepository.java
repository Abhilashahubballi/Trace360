package com.trace360.repository;

import com.trace360.entity.DeliveryConfirmation;
import com.trace360.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DeliveryConfirmationRepository extends JpaRepository<DeliveryConfirmation, Long> {

    @Query("SELECT d FROM DeliveryConfirmation d WHERE d.aPackage = :pkg")
    Optional<DeliveryConfirmation> findByAPackage(@Param("pkg") Package pkg);
}
