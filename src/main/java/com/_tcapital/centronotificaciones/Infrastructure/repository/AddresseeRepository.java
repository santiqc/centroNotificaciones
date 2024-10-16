package com._tcapital.centronotificaciones.Infrastructure.repository;

import com._tcapital.centronotificaciones.domain.Addressee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddresseeRepository extends JpaRepository<Addressee, Long> {
    Addressee findByTrackingId(String trackingId);
}
