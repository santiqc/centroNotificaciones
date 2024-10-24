package com._tcapital.centronotificaciones.application.mapper;

import com._tcapital.centronotificaciones.application.Dto.EmailDto;
import com._tcapital.centronotificaciones.domain.Email;

public class EmailMapper {

    public static EmailDto toDTO(Email email, boolean mapAddresseeEmail) {
        if (email == null) {
            return null;
        }

        EmailDto emailDto = new EmailDto();
        emailDto.setId(email.getId());
        emailDto.setSince(email.getSince());
        emailDto.setForTo(email.getForTo());
        emailDto.setCc(email.getCc());
        emailDto.setBcc(email.getBcc());
        emailDto.setSubject(email.getSubject());
        emailDto.setBody(email.getBody());
        emailDto.setEvent(email.getEvent());
        emailDto.setReason(email.getReason());
        emailDto.setIdHistory(email.getIdHistory());
        emailDto.setTrackingId(email.getTrackingId());
        emailDto.setIsLargeMail(email.getIsLargeMail());
        emailDto.setSentAt(email.getSentAt());
        emailDto.setEventdate(email.getEventdate());
        emailDto.setIsCertificate(email.getIsCertificate());
        emailDto.setStatus(email.getStatus());
        return emailDto;
    }


    public static EmailDto toDTO(Email email) {
        return toDTO(email, false);
    }

    public static Email toEntity(EmailDto emailDto) {
        if (emailDto == null) {
            return null;
        }

        return Email.builder()
                .id(emailDto.getId())
                .since(emailDto.getSince())
                .forTo(emailDto.getForTo())
                .cc(emailDto.getCc())
                .bcc(emailDto.getBcc())
                .subject(emailDto.getSubject())
                .body(emailDto.getBody())
                .event(emailDto.getEvent())
                .reason(emailDto.getReason())
                .idHistory(emailDto.getIdHistory())
                .trackingId(emailDto.getTrackingId())
                .isLargeMail(emailDto.getIsLargeMail())
                .sentAt(emailDto.getSentAt())
                .eventdate(emailDto.getEventdate())
                .isCertificate(emailDto.getIsCertificate())
                .status(emailDto.getStatus())
                .build();
    }
}