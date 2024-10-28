package com._tcapital.centronotificaciones.Infrastructure.Adapter;


import com._tcapital.centronotificaciones.Infrastructure.repository.AddresseeRepository;
import com._tcapital.centronotificaciones.Infrastructure.repository.ApplicationRepository;
import com._tcapital.centronotificaciones.Infrastructure.repository.FilesRepository;
import com._tcapital.centronotificaciones.Infrastructure.repository.JpaEmailRepository;
import com._tcapital.centronotificaciones.domain.Addressee;
import com._tcapital.centronotificaciones.domain.Application;
import com._tcapital.centronotificaciones.domain.Email;
import com._tcapital.centronotificaciones.domain.Files;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class EmailPersistenceAdapter {
    private final JpaEmailRepository emailRepository;
    private final AddresseeRepository addresseeRepository;
    private final FilesRepository filesRepository;
    private final ApplicationRepository applicationRepository;

    @Autowired
    public EmailPersistenceAdapter(JpaEmailRepository emailRepository, AddresseeRepository addresseeRepository, FilesRepository filesRepository, ApplicationRepository applicationRepository) {
        this.emailRepository = emailRepository;
        this.addresseeRepository = addresseeRepository;
        this.filesRepository = filesRepository;
        this.applicationRepository = applicationRepository;
    }

    public void saveEmail(Email email) {
        emailRepository.save(email);
    }

    public Optional<Email> findEmailByTrackingId(String trackingId) {
        return emailRepository.findByTrackingId(trackingId);
    }

    public Page<Email> findByRecipient(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return emailRepository.filterEmails(pageable);
    }

    public Addressee findAddresseeByTrackingId(String trackingId) {
        return addresseeRepository.findByTrackingId(trackingId);
    }

    public List<Addressee> findAddresseeByIdEmail(Long emailId) {
        return addresseeRepository.findByEmail(emailId);
    }

    public List<Files> findFilesByTrackingId(String trackingId) {
        return filesRepository.findByTrackingId(trackingId);
    }

    public Page<Email> filterEmailsByStatusCcAndProcess(String status, String cc, String process, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return emailRepository.filterEmailsByStatusCcAndProcess(status, cc, process, pageable);
    }

    public Application saveApplication(Application application) {
        return applicationRepository.save(application);
    }

    public Addressee saveAddressee(Addressee addressee) {
        return addresseeRepository.save(addressee);
    }
    public Object saveFiles(List<Files> files) {
        return filesRepository.saveAll(files);
    }


}
