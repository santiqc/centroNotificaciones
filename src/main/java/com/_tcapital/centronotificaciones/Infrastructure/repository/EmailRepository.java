package com._tcapital.centronotificaciones.Infrastructure.repository;

import com._tcapital.centronotificaciones.domain.Email;
import org.springframework.data.domain.Page;

public interface EmailRepository {
    void save(Email email);
    Page<Email> filterEmails(String recipient);
}
