package com._tcapital.centronotificaciones.application.Dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestEmailDto {
    private String from;
    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private String body;
    private List<String> attachments;
    private Boolean isLargeMail;
}
