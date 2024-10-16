package com._tcapital.centronotificaciones.application.mapper;

import com._tcapital.centronotificaciones.application.Dto.EmailDto;
import com._tcapital.centronotificaciones.domain.Email;

public class EmailMapper {

    public static EmailDto toDTO(Email email) {
        if (email == null) {
            return null;
        }

        EmailDto dto = new EmailDto();
        dto.setId(email.getId());
        dto.setFrom(email.getSince());
        dto.setTo(email.getForTo());
        dto.setCc(email.getCc());
        dto.setBcc(email.getBcc());
        dto.setSubject(email.getSubject());
        dto.setBody(email.getBody());
        dto.setIsLargeMail(email.getIsLargeMail());
        dto.setSentAt(email.getSentAt());
        dto.setIsCertificated(email.getIsCertificate());
        return dto;
    }

    public static Email toEntity(EmailDto dto) {
        if (dto == null) {
            return null;
        }

        Email email = new Email();
        email.setId(dto.getId());
        email.setSince(dto.getFrom());
        email.setForTo(dto.getTo());
        email.setCc(dto.getCc());
        email.setBcc(dto.getBcc());
        email.setSubject(dto.getSubject());
        email.setBody(dto.getBody());
        email.setIsLargeMail(dto.getIsLargeMail());
        email.setSentAt(dto.getSentAt());
        email.setIsCertificate(dto.getIsCertificated());
        return email;
    }
}
