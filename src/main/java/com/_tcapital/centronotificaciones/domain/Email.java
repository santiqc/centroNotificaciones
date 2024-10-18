package com._tcapital.centronotificaciones.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String since;
    private String forTo;
    private String cc;
    private String bcc;
    private String subject;
    private String body;
    private String trackingId;
    private Boolean isLargeMail;
    private LocalDateTime sentAt;
    private Boolean isCertificate;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Application application;

}