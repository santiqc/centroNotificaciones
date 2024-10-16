package com._tcapital.centronotificaciones.application;

import com._tcapital.centronotificaciones.Infrastructure.Adapter.EmailPersistenceAdapter;
import com._tcapital.centronotificaciones.Infrastructure.Adapter.LoginAdapter;
import com._tcapital.centronotificaciones.Infrastructure.Adapter.SendGridAdapter;
import com._tcapital.centronotificaciones.application.Dto.EmailDto;
import com._tcapital.centronotificaciones.application.mapper.EmailMapper;
import com._tcapital.centronotificaciones.domain.Addressee;
import com._tcapital.centronotificaciones.domain.Email;
import com._tcapital.centronotificaciones.domain.Files;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmailServiceImpl implements EmailService {

    private final EmailPersistenceAdapter emailPersistenceAdapter;
    private final SendGridAdapter sendGridAdapter;
    private final LoginAdapter loginAdapter;

    public EmailServiceImpl(EmailPersistenceAdapter emailPersistenceAdapter, SendGridAdapter sendGridAdapter, LoginAdapter loginAdapter) {
        this.emailPersistenceAdapter = emailPersistenceAdapter;
        this.sendGridAdapter = sendGridAdapter;
        this.loginAdapter = loginAdapter;
    }

    @Override
    public void sendEmail(EmailDto emailRequest) {
        // sendGridAdapter.sendEmail(recipient, subject, body);

        Email email = EmailMapper.toEntity(emailRequest);
        emailPersistenceAdapter.saveEmail(email);
    }

    //    @Override
//    public List<EmailDto> getSentEmails(String recipient) {
//        Page<Email> emails =  emailPersistenceAdapter.findByRecipient(1);
//        return emails.stream()
//                .map(EmailMapper::toDTO)
//                .collect(Collectors.toList());
//    }
    @Override
    public List<EmailDto> getSentEmails(String status, String cc, String process, Integer pageNo, Integer pageSize) {
        Page<Email> emails = emailPersistenceAdapter.filterEmailsByStatusCcAndProcess(status, cc, process, pageNo, pageSize);

        return emails.stream()
                .map(email -> {
                    Addressee addressee = emailPersistenceAdapter.findAddresseeByTrackingId(email.getTrackingId());
                    List<Files> files = emailPersistenceAdapter.findFilesByTrackingId(email.getTrackingId());

                    EmailDto emailDto = EmailMapper.toDTO(email);

                    if (addressee != null) {
                        emailDto.setAddressee(addressee);
                    }

                    if (files != null && !files.isEmpty()) {
                        emailDto.setFiles(files);
                    }

                    return emailDto;
                })
                .collect(Collectors.toList());
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
