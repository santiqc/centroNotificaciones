package com._tcapital.centronotificaciones.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Email implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String since;
    private String forTo;
    private String cc;
    private String bcc;
    private String subject;

    @Lob
    private String body;

    private String event;
    private String reason;
    private Long idHistory;
    private String trackingId;
    private Boolean isLargeMail;
    private LocalDateTime sentAt;
    private LocalDateTime eventdate;
    private Boolean isCertificate;
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    private Application application;

}