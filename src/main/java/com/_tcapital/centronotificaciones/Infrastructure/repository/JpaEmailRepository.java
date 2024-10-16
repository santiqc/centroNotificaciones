package com._tcapital.centronotificaciones.Infrastructure.repository;

import com._tcapital.centronotificaciones.domain.Email;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaEmailRepository extends JpaRepository<Email, Long> {
    @Query("SELECT e FROM Email e")
    Page<Email> filterEmails(Pageable pageable);


    @Query("SELECT e FROM Email e JOIN Addressee a ON e.trackingId = a.trackingId " +
            "WHERE (:status IS NULL OR e.status = :status) " +
            "AND (:cc IS NULL OR e.cc = :cc) " +
            "AND (:process IS NULL OR a.process = :process)")
    Page<Email> filterEmailsByStatusCcAndProcess(@Param("status") String status,
                                                 @Param("cc") String cc,
                                                 @Param("process") String process,
                                                 Pageable pageable);
}
