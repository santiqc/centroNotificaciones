package com._tcapital.centronotificaciones.Infrastructure.repository;

import com._tcapital.centronotificaciones.domain.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaEmailRepository extends JpaRepository<Email, Long> {
    @Transactional
    @Query("SELECT e FROM Email e")
    Page<Email> filterEmails(Pageable pageable);

    @Transactional
    @Query("SELECT e FROM Email e LEFT JOIN Addressee a ON e.id = a.email.id " +
            "WHERE (:status IS NULL OR e.status = :status) " +
            "AND (:cc IS NULL OR e.cc = :cc) " +
            "AND (:process IS NULL OR a.process = :process)")
    Page<Email> filterEmailsByStatusCcAndProcess(@Param("status") String status,
                                                 @Param("cc") String cc,
                                                 @Param("process") String process,
                                                 Pageable pageable);

    @Transactional
    @Query("SELECT e FROM Email e WHERE e.trackingId = :trackingId")
    Optional<Email> findByTrackingId(@Param("trackingId") String trackingId);

    @Transactional
    @Query("SELECT e FROM Email e WHERE e.idHistory = :idHistory OR e.trackingId = :trackingId")
    Optional<Email> findByIdHistoryOrTrackingId(@Param("idHistory") Long idHistory,
                                                @Param("trackingId") String trackingId);

}
