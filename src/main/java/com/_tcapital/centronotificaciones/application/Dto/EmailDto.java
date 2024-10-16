package com._tcapital.centronotificaciones.application.Dto;

import com._tcapital.centronotificaciones.domain.Addressee;
import com._tcapital.centronotificaciones.domain.Files;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EmailDto {
    private Long id;
    private String from;
    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private String body;
    private List<Files>files;
    private Boolean isLargeMail;
    private LocalDateTime sentAt;
    private Boolean isCertificated;
    private Addressee addressee;
}
