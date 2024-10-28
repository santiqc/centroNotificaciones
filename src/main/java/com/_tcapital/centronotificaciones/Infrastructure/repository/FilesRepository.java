package com._tcapital.centronotificaciones.Infrastructure.repository;

import com._tcapital.centronotificaciones.domain.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Repository
public interface FilesRepository extends JpaRepository<Files, Long> {


    @Query("SELECT f FROM Files f WHERE f.trackingId = :trackingId")
    List<Files> findFilesByTrackingId(@Param("trackingId") String trackingId);
}