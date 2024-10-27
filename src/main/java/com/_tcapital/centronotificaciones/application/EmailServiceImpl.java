package com._tcapital.centronotificaciones.application;

import com._tcapital.centronotificaciones.Infrastructure.Adapter.EmailPersistenceAdapter;
import com._tcapital.centronotificaciones.Infrastructure.Adapter.SendGridAdapter;
import com._tcapital.centronotificaciones.application.Dto.*;
import com._tcapital.centronotificaciones.application.mapper.EmailMapper;
import com._tcapital.centronotificaciones.domain.Addressee;
import com._tcapital.centronotificaciones.domain.Email;
import com._tcapital.centronotificaciones.domain.Files;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final EmailPersistenceAdapter emailPersistenceAdapter;
    private final SendGridAdapter sendGridAdapter;
    private final LoginService loginService;

    ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("America/Bogota"));

    public EmailServiceImpl(EmailPersistenceAdapter emailPersistenceAdapter, SendGridAdapter sendGridAdapter, LoginService loginService) {
        this.emailPersistenceAdapter = emailPersistenceAdapter;
        this.sendGridAdapter = sendGridAdapter;
        this.loginService = loginService;
    }

    @Override
    public EmailResponseDto sendEmail(RequestEmailDto emailRequest) {
        LoginCamerResponse login = loginService.login();
        String token = login.getData().getAttributes().getAccessToken();

        EmailResponseDto resp = sendGridAdapter.sendEmail(token, emailRequest);
        emailPersistenceAdapter.saveEmail(Email.builder()
                .since(emailRequest.getFrom())
                .forTo(emailRequest.getTo())
                .cc(emailRequest.getCc())
                .bcc(emailRequest.getBcc())
                .subject(emailRequest.getSubject())
                .body(emailRequest.getBody())
                .trackingId(resp.getData().getAttributes().getTrackingID())
                .isLargeMail(emailRequest.getIsLargeMail())
                .sentAt(zonedDateTime.toLocalDateTime())
                .isCertificate(true)
                .status(null)
                .build());
        return resp;


    }

    @Override
    public List<EmailDto> getSentEmails(String status, String cc, String process, Integer pageNo, Integer pageSize) {
        Page<Email> emails = emailPersistenceAdapter.filterEmailsByStatusCcAndProcess(status, cc, process, pageNo, pageSize);
        if (emails.isEmpty()) {
            return Collections.emptyList();
        }
        return emails.stream()
                .map(email -> {
                    List<Addressee> addressee = emailPersistenceAdapter.findAddresseeByIdEmail(email.getId());
                    List<Files> files = emailPersistenceAdapter.findFilesByTrackingId(email.getTrackingId());

                    EmailDto emailDto = EmailMapper.toDTO(email);

                    if (addressee != null) {
                        emailDto.setAddressee(addressee);
                    }

                    if (files != null && !files.isEmpty()) {
                        emailDto.setFiles(files);
                    }
                    emailDto.setWitness(Boolean.TRUE);
                    emailDto.setAttachments(0);

                    return emailDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public FileUploadResponse uploadFile(MultipartFile file) {
        log.info("Initiating file upload for file: {}", file.getOriginalFilename());
        LoginCamerResponse login = loginService.login();
        String token = login.getData().getAttributes().getAccessToken();

        String response = sendGridAdapter.uploadFile(file, token);

        return FileUploadResponse.builder()
                .data(FileUploadResponse.FileData.builder()
                        .type("file")
                        .fileName(file.getOriginalFilename())
                        .attributes(FileUploadResponse.Attributes.builder()
                                .response(response)
                                .build())
                        .build())
                .build();
    }

    public static Specification<Email> filterByStatusAndCcAndProcess(String status, String cc, String process) {
        return (Root<Email> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();


            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }


            if (cc != null) {
                predicates.add(criteriaBuilder.equal(root.get("cc"), cc));
            }


            if (process != null) {
                Join<Email, Addressee> addresseeJoin = root.join("addressee");
                predicates.add(criteriaBuilder.equal(addresseeJoin.get("process"), process));
            }


            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
