package com._tcapital.centronotificaciones.application;

import com._tcapital.centronotificaciones.Infrastructure.Adapter.EmailPersistenceAdapter;
import com._tcapital.centronotificaciones.Infrastructure.Adapter.SendGridAdapter;
import com._tcapital.centronotificaciones.Infrastructure.exception.EmailSendException;
import com._tcapital.centronotificaciones.application.Dto.*;
import com._tcapital.centronotificaciones.application.mapper.EmailMapper;
import com._tcapital.centronotificaciones.domain.Addressee;
import com._tcapital.centronotificaciones.domain.Application;
import com._tcapital.centronotificaciones.domain.Email;
import com._tcapital.centronotificaciones.domain.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
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
        log.info("Starting email sending process for application: {}", emailRequest.getNameApplication());

        try {
            LoginCamerResponse login = loginService.login();
            String token = login.getData().getAttributes().getAccessToken();
            log.debug("Token acquired for email sending.");


            EmailResponseDto response = sendGridAdapter.sendEmail(token, emailRequest);
            log.info("Email sent successfully with tracking ID: {}", response.getData().getAttributes().getTrackingID());


            Application application = new Application();
            application.setName(emailRequest.getNameApplication());
            Application savedApplication = emailPersistenceAdapter.saveApplication(application);

            String trackingID = response.getData().getAttributes().getTrackingID();

            if (savedApplication != null && savedApplication.getId() != null) {
                log.info("Application saved successfully with ID: {}", savedApplication.getId());

                Email email = Email.builder()
                        .since(emailRequest.getFrom())
                        .forTo(emailRequest.getTo())
                        .cc(emailRequest.getCc())
                        .bcc(emailRequest.getBcc())
                        .subject(emailRequest.getSubject())
                        .body(emailRequest.getBody())
                        .trackingId(trackingID)
                        .isLargeMail(emailRequest.getIsLargeMail())
                        .sentAt(zonedDateTime.toLocalDateTime())
                        .eventdate(zonedDateTime.toLocalDateTime())
                        .isCertificate(true)
                        .status(null)
                        .application(savedApplication)
                        .build();

                emailPersistenceAdapter.saveEmail(email);
                log.info("Email record saved successfully in the database.");
            } else {
                log.warn("Application was not saved; skipping email record saving.");
            }

            return response;

        } catch (Exception e) {
            log.error("An error occurred during email sending process for application: {}", emailRequest.getNameApplication(), e);
            throw new EmailSendException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
//                        emailDto.setFiles(files);
                        List<Map<String, Object>> fileDetailsList = files.stream()
                                .map(file -> {
                                    Map<String, Object> fileMap = new HashMap<>();
                                    fileMap.put("base64", Base64.getEncoder().encodeToString(file.getFileData()));
                                    fileMap.put("nameFile", file.getNameFile());
                                    fileMap.put("contentType", file.getContentType());
                                    fileMap.put("fileSize", file.getFileSize());
                                    fileMap.put("uploadDate", file.getUploadDate());
                                    fileMap.put("witness", file.getWitness());
                                    return fileMap;
                                })
                                .collect(Collectors.toList());
                        emailDto.setFiles(fileDetailsList);
                    }

                    Long attachmentCount = files.stream()
                            .filter(file -> Boolean.FALSE.equals(file.getWitness()))
                            .count();
                    emailDto.setAttachments(attachmentCount);

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

    @Override
    public Object updateInfoAddresseeAndFiles(String trackingId, RequestAddresseeDto emailRequest) throws EmailSendException {
        try {

            Email email = emailPersistenceAdapter.findEmailByTrackingId(trackingId)
                    .orElseThrow(() -> new EmailSendException("Email not found with tracking ID: " + trackingId, HttpStatus.BAD_REQUEST));

            log.info("Found email with tracking ID: {}", trackingId);


            Addressee addressee = new Addressee();
            addressee.setEmail(email);
            addressee.setName(emailRequest.getName());
            addressee.setProcess(emailRequest.getProcess());
            addressee.setTrackingId(email.getTrackingId());

            List<MultipartFile> filesAux = emailRequest.getFile();
            List<Files> documentos = new ArrayList<>();

            if (filesAux != null && !filesAux.isEmpty()) {
                log.info("Processing {} files for tracking ID: {}", filesAux.size(), trackingId);
                for (MultipartFile aux : filesAux) {
                    Files file = new Files();
                    file.setNameFile(aux.getOriginalFilename());
                    file.setTrackingId(trackingId);
                    file.setUploadDate(zonedDateTime.toLocalDateTime());
                    file.setContentType(aux.getContentType());
                    file.setWitness(Boolean.FALSE);
                    file.setFileData(aux.getBytes());
                    file.setFileSize(aux.getSize());
                    documentos.add(file);
                    log.info("File {} processed and added to the list.", aux.getOriginalFilename());
                }
            } else {
                log.info("No files provided for tracking ID: {}", trackingId);
            }


            emailPersistenceAdapter.saveAddressee(addressee);
            log.info("Addressee updated successfully for tracking ID: {}", trackingId);


            if (!documentos.isEmpty()) {
                emailPersistenceAdapter.saveFiles(documentos);
                log.info("{} files saved successfully for tracking ID: {}", documentos.size(), trackingId);
            } else {
                log.info("No files to save for tracking ID: {}", trackingId);
            }

            return Map.of(
                    "message", "Update info for addressee was successful",
                    "addresseeId", addressee.getId(),
                    "filesCount", documentos.size()
            );

        } catch (EmailSendException e) {
            log.error("Error processing email for tracking ID {} (EmailSendException): {}", trackingId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while updating addressee and files for tracking ID {}: {}", trackingId, e);
            throw new EmailSendException("Unexpected error during update: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Object findByIdHistoryOrTrackingId(Long idHistory, String trackingId) throws EmailSendException {
        try {
            Email email = emailPersistenceAdapter.findByIdHistoryOrTrackingId(idHistory, trackingId)
                    .orElseThrow(() -> new EmailSendException("Email not found with tracking ID: " + trackingId, HttpStatus.BAD_REQUEST));

            List<Files> files = emailPersistenceAdapter.findFilesByTrackingId(email.getTrackingId());


            List<Map<String, Object>> fileDetailsList = files.stream()
                    .map(file -> {
                        Map<String, Object> fileMap = new HashMap<>();
                        fileMap.put("base64", Base64.getEncoder().encodeToString(file.getFileData()));
                        fileMap.put("nameFile", file.getNameFile());
                        fileMap.put("contentType", file.getContentType());
                        fileMap.put("fileSize", file.getFileSize());
                        fileMap.put("uploadDate", file.getUploadDate());
                        fileMap.put("witness", file.getWitness());
                        return fileMap;
                    })
                    .collect(Collectors.toList());

            long attachmentCount = files.stream()
                    .filter(file -> Boolean.FALSE.equals(file.getWitness()))
                    .count();


            Map<String, Object> response = new HashMap<>();
            response.put("message", "Email and file retrieval successful");
            response.put("trackingId", email.getTrackingId());
            response.put("attachmentCount", attachmentCount);
            response.put("idHistory", email.getIdHistory());
            response.put("files", fileDetailsList);

            return response;
        } catch (EmailSendException e) {
            log.error("Error processing email for tracking ID {} (EmailSendException): {}", trackingId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while retrieving email and files for tracking ID {}: {}", trackingId, e);
            throw new EmailSendException("Unexpected error during retrieval: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public EmailResponseDto sendEmail(RequestEmailCompletDto emailRequest) throws EmailSendException {
        log.info("Starting email sending process for application: {}", emailRequest.getNameApplication());

        try {
            LoginCamerResponse login = loginService.login();
            String token = login.getData().getAttributes().getAccessToken();
            log.debug("Token acquired for email sending.");


            RequestEmailDto requestEmailDto = new RequestEmailDto();
            requestEmailDto.setFrom(emailRequest.getFrom());
            requestEmailDto.setTo(emailRequest.getTo());
            requestEmailDto.setBcc(emailRequest.getBcc());
            requestEmailDto.setSubject(emailRequest.getSubject());
            requestEmailDto.setBody(emailRequest.getBody());
            requestEmailDto.setAttachments(emailRequest.getAttachments());
            requestEmailDto.setIsLargeMail(emailRequest.getIsLargeMail());

            EmailResponseDto response = sendGridAdapter.sendEmail(token, requestEmailDto);
            log.info("Email sent successfully with tracking ID: {}", response.getData().getAttributes().getTrackingID());


            Application application = new Application();
            application.setName(emailRequest.getNameApplication());
            Application savedApplication = emailPersistenceAdapter.saveApplication(application);

            String trackingID = response.getData().getAttributes().getTrackingID();

            if (savedApplication != null && savedApplication.getId() != null) {
                log.info("Application saved successfully with ID: {}", savedApplication.getId());

                Email email = Email.builder()
                        .since(emailRequest.getFrom())
                        .forTo(emailRequest.getTo())
                        .cc(emailRequest.getCc())
                        .bcc(emailRequest.getBcc())
                        .subject(emailRequest.getSubject())
                        .body(emailRequest.getBody())
                        .trackingId(trackingID)
                        .isLargeMail(emailRequest.getIsLargeMail())
                        .sentAt(zonedDateTime.toLocalDateTime())
                        .eventdate(zonedDateTime.toLocalDateTime())
                        .isCertificate(true)
                        .status(null)
                        .application(savedApplication)
                        .event(emailRequest.getEvent())
                        .reason(emailRequest.getReason())
                        .build();

                Email emailSaved = emailPersistenceAdapter.saveEmail(email);
                RequestAddresseeDto dataAddressee = new RequestAddresseeDto();
                dataAddressee.setName(emailRequest.getNameAddressee());
                dataAddressee.setProcess(emailRequest.getProcessAddressee());
                dataAddressee.setFile(emailRequest.getFiles());

                //update AddresseeAndFiles
                updateInfoAddresseeAndFiles(emailSaved.getTrackingId(), dataAddressee);

                log.info("Email record saved successfully in the database.");
            } else {
                log.warn("Application was not saved; skipping email record saving.");
            }

            return response;

        } catch (Exception e) {
            log.error("An error occurred during email sending process for application: {}", emailRequest.getNameApplication(), e);
            throw new EmailSendException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Object generateUsageReport(UsageReportRequest request) throws EmailSendException {
        log.info("Initiating generateUsageReport: {}", request);
        LoginCamerResponse login = loginService.login();
        String token = login.getData().getAttributes().getAccessToken();
        String tokenAdmin = login.getDataAdmin().getAccessToken();

        Object response = sendGridAdapter.generateUsageReport(request, token, tokenAdmin);

        return Map.of(
                "data", response
        );
    }

    @Override
    public MessageStatusResponse getMessageStatus(String trackingId) throws EmailSendException {
        log.info("Initiating getMessageStatus: {}", trackingId);
        LoginCamerResponse login = loginService.login();
        String token = login.getData().getAttributes().getAccessToken();
        String tokenAdmin = login.getDataAdmin().getAccessToken();
        return sendGridAdapter.getMessageStatus(trackingId, token, tokenAdmin);
    }

    @Override
    public ReceiptResponse getReceiptByTrackId(String trackingId) throws EmailSendException {
        log.info("Initiating getReceiptByTrackId: {}", trackingId);
        LoginCamerResponse login = loginService.login();
        String token = login.getData().getAttributes().getAccessToken();
        return sendGridAdapter.getReceiptByTrackId(trackingId, token);
    }
}
