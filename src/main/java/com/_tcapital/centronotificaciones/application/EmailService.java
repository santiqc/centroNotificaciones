package com._tcapital.centronotificaciones.application;

import com._tcapital.centronotificaciones.Infrastructure.exception.EmailSendException;
import com._tcapital.centronotificaciones.application.Dto.*;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmailService {
    EmailResponseDto sendEmail(RequestEmailDto email) throws EmailSendException;

    List<EmailDto> getSentEmails(String status, String cc, String process, Integer pageNo, Integer pageSize);

    FileUploadResponse uploadFile(MultipartFile file) throws EmailSendException;

    Object updateInfoAddresseeAndFiles(String trackingId, RequestAddresseeDto emailRequest) throws EmailSendException;

    Object findByIdHistoryOrTrackingId(Long idHistory, String trackingId) throws EmailSendException;

    EmailResponseDto sendEmail(RequestEmailCompletDto emailRequest) throws EmailSendException;

    Object generateUsageReport(UsageReportRequest request) throws EmailSendException;

    MessageStatusResponse getMessageStatus(String trackingId) throws EmailSendException;

    ReceiptResponse getReceiptByTrackId(String trackingId) throws EmailSendException;

}
