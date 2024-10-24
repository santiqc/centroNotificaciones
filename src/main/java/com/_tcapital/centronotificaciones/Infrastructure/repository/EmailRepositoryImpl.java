package com._tcapital.centronotificaciones.Infrastructure.repository;

import com._tcapital.centronotificaciones.domain.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmailRepositoryImpl implements EmailRepository {
    private final JpaEmailRepository jpaEmailRepository;

    @Autowired
    public EmailRepositoryImpl(JpaEmailRepository jpaEmailRepository) {
        this.jpaEmailRepository = jpaEmailRepository;
    }

    @Override
    public void save(Email email) {
        jpaEmailRepository.save(email);
    }

    @Override
    public Page<Email> filterEmails(String recipient) {
        Pageable pageable = PageRequest.of(1, 50);
        return jpaEmailRepository.filterEmails(pageable);
    }

    @Override
    public void saveAll(List<Email> emails) {
        if (emails != null && !emails.isEmpty()) {
            jpaEmailRepository.saveAll(emails);
        } else {
            System.out.println("La lista de correos está vacía o es nula.");
        }
    }


}
