package com._tcapital.centronotificaciones.Infrastructure.repository;

import com._tcapital.centronotificaciones.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
}
