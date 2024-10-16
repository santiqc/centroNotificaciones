package com._tcapital.centronotificaciones.application;

import com._tcapital.centronotificaciones.application.Dto.EmailDto;

import java.util.List;

public interface EmailService {
    void sendEmail(EmailDto email);
    List<EmailDto> getSentEmails(String status, String cc, String process, Integer pageNo, Integer pageSize);
}
