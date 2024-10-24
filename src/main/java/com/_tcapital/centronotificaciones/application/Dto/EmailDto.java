package com._tcapital.centronotificaciones.application.Dto;

import com._tcapital.centronotificaciones.domain.Addressee;
import com._tcapital.centronotificaciones.domain.Files;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class EmailDto {
    private Long id;
    private String since;
    private String forTo;
    private String cc;
    private String bcc;
    private String subject;

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

    private List<Files>files;
    private List<Addressee> addressee;


}
