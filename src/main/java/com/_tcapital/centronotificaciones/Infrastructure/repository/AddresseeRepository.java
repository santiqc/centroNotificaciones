package com._tcapital.centronotificaciones.Infrastructure.repository;

import com._tcapital.centronotificaciones.domain.Addressee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddresseeRepository extends JpaRepository<Addressee, Long> {
    Addressee findByTrackingId(String trackingId);

    @Query("SELECT e FROM Addressee e " +
            "WHERE (:emailId IS NULL OR e.email.id = :emailId)")
    List<Addressee> findByEmail(@Param("emailId") Long emailId);

}
