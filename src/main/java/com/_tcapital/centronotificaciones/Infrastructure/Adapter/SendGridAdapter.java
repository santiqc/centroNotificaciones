package com._tcapital.centronotificaciones.Infrastructure.Adapter;

import com._tcapital.centronotificaciones.Infrastructure.exception.EmailSendException;
import com._tcapital.centronotificaciones.application.Dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SendGridAdapter {
    @Value("${sendgrid.api.key}")
    private String rpostApiUrl;

    @Value("${camer.api}")
    private String apiDomain;

    private final RestTemplate restTemplate;

    public SendGridAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public EmailResponseDto sendEmail(String token, RequestEmailDto emailRequest) {
        try {

            if (token == null || token.trim().isEmpty()) {
                throw new EmailSendException("Failed to send email due to invalid token", HttpStatus.BAD_REQUEST);
            }


            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);


            Map<String, Object> mailData = new HashMap<>();
            mailData.put("From", emailRequest.getFrom());


            mailData.put("To", processEmailList(emailRequest.getTo()));
            mailData.put("Cc", processEmailList(emailRequest.getCc()));
            mailData.put("Bcc", processEmailList(emailRequest.getBcc()));

            mailData.put("Subject", emailRequest.getSubject());
            mailData.put("Body", emailRequest.getBody());
            mailData.put("Attachments", emailRequest.getAttachments() != null ? emailRequest.getAttachments() : new ArrayList<>());
            mailData.put("IsLargeMail", emailRequest.getIsLargeMail());


            log.info("Request body: {}", mailData);


            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(mailData, headers);


            ResponseEntity<RPostResponse> response = restTemplate.postForEntity(
                    rpostApiUrl,
                    requestEntity,
                    RPostResponse.class
            );
            log.info("Request body: {}", response);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new EmailSendException("Failed to send email through RPost", HttpStatus.BAD_REQUEST);
            }

            RPostResponse apiResponse = response.getBody();
            return convertToEmailResponseDto(apiResponse);

        } catch (EmailSendException e) {
            log.error("Error sending email through RPost (EmailSendException): {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Error sending email through RPost", e);
            throw new EmailSendException("Error sending email through RPost " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    private String processEmailList(String emails) {
        if (emails == null || emails.trim().isEmpty()) {
            return "";
        }
        return Arrays.stream(emails.split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(";"));
    }

    public String uploadFile(MultipartFile file, String token) {
        try {
            String uploadUrl = apiDomain + "api/Upload";
            log.info("Uploading file to URL: {}", uploadUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            Resource fileResource = createFileResource(file);
            log.info("File resource created for upload: {}", file.getOriginalFilename());

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    uploadUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );
            log.info("Upload response received with status: {}", response.getStatusCode());
            log.debug("Response body: {}", response.getBody());

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.warn("Upload failed with status: {}", response.getStatusCode());
                throw new EmailSendException("Upload failed with status: " + response.getStatusCode(), HttpStatus.BAD_REQUEST);
            }

            return response.getBody();

        } catch (EmailSendException e) {
            log.error("Error during file upload (EmailSendException): {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during file upload: {}", e.getMessage(), e);
            throw new EmailSendException("Unexpected error during file upload", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Resource createFileResource(MultipartFile file) throws Exception {
        return new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
    }


    public Object generateUsageReport(UsageReportRequest request, String token, String adminToken) {
        log.info("Generating usage report with service");

        if (token == null) {
            throw new EmailSendException("Not authenticated. Please provide a valid token.", HttpStatus.UNAUTHORIZED);
        }

        String reportUrl = apiDomain + "api/Reports/UsageReport";

        token = adminToken;
        log.debug("Using token: {}", token);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<UsageReportRequest> requestEntity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    reportUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Object.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new EmailSendException("Failed to generate report: " + response.getStatusCode(), HttpStatus.BAD_REQUEST);
            }

            return response.getBody();

        } catch (EmailSendException e) {
            log.error("Error calling usage report API", e);
            throw e;
        } catch (Exception e) {
            log.error("Error calling usage report API", e);
            throw new EmailSendException("Failed to generate usage report", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public MessageStatusResponse getMessageStatus(String trackingId, String token, String adminToken) {
        String messageStatusUrl = apiDomain + "api/v1/Receipt/MessageStatus";
        token = adminToken;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        MessageStatusResponse.MessageStatusRequest request = new MessageStatusResponse.MessageStatusRequest();
        request.setTrackingId(trackingId);
        HttpEntity<MessageStatusResponse.MessageStatusRequest> requestEntity = new HttpEntity<>(request, headers);

        int maxRetries = 3;  // Número máximo de intentos
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
                factory.setReadTimeout(5000);  // Timeout de 5 segundos
                RestTemplate client = new RestTemplate(factory);

                ResponseEntity<Object> response = client.exchange(
                        messageStatusUrl,
                        HttpMethod.POST,
                        requestEntity,
                        Object.class
                );

                if (!response.getStatusCode().is2xxSuccessful()) {
                    throw new EmailSendException("Failed to get message status: " + response.getStatusCode(), HttpStatus.BAD_REQUEST);
                }

                Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
                Map<String, Object> resultContent = (Map<String, Object>) ((List<?>) responseBody.get("ResultContent")).get(0);
                String trackingIds = (String) resultContent.get("TrackingId");

                String receiptUrl = apiDomain + "api/v1/Receipt/" + trackingIds;
                ResponseEntity<byte[]> receiptResponse = client.exchange(
                        receiptUrl,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        byte[].class
                );

                MessageStatusResponse.ReceiptFile receiptFile = MessageStatusResponse.ReceiptFile.builder()
                        .content(new String(receiptResponse.getBody(), StandardCharsets.ISO_8859_1))
                        .mediaType("application/zip")
                        .build();

                return MessageStatusResponse.builder()
                        .messageStatus(MessageStatusResponse.MessageStatus.builder()
                                .status((String) responseBody.get("Status"))
                                .statusCode((int) responseBody.get("StatusCode"))
                                .statusText((String) responseBody.get("StatusText"))
                                .message((List<MessageStatusResponse.Message>) responseBody.get("Message"))
                                .resultContent(Collections.singletonList(MessageStatusResponse.ResultContent.builder()
                                        .trackingId((String) resultContent.get("TrackingId"))
                                        .customerTrackingId((String) resultContent.get("CustomerTrackingId"))
                                        .senderName((String) resultContent.get("SenderName"))
                                        .senderAddress((String) resultContent.get("SenderAddress"))
                                        .date((String) resultContent.get("Date"))
                                        .status((String) resultContent.get("Status"))
                                        .recipients(((List<Map<String, Object>>) resultContent.get("Recipients")).stream()
                                                .map(r -> MessageStatusResponse.Recipient.builder()
                                                        .address((String) r.get("Address"))
                                                        .recipientType((String) r.get("RecipientType"))
                                                        .deliveredDate((String) r.get("DeliveredDate"))
                                                        .openedDate((String) r.get("OpenedDate"))
                                                        .deliveryStatus((String) r.get("DeliveryStatus"))
                                                        .deliveryDetail((String) r.get("DeliveryDetail"))
                                                        .build())
                                                .collect(Collectors.toList()))
                                        .redactedTextViewDetail((String) resultContent.get("RedactedTextViewDetail"))
                                        .build()))
                                .build())
                        .receiptFile(receiptFile)
                        .build();

            } catch (EmailSendException e) {
                log.error("Error getting message status", e);
                throw e;
            } catch (Exception e) {
                attempt++;
                log.error("Attempt " + attempt + " failed. Retrying...", e);
                if (attempt >= maxRetries) {
                    log.error("Max retries reached. Failing.");
                    throw new EmailSendException("Failed to get message status after retries", e, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                try {
                    Thread.sleep(1000 * attempt); // Retraso exponencial: 1 segundo en el primer intento, 2 segundos en el segundo, etc.
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new EmailSendException("Interrupted during retry delay", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
        throw new EmailSendException("Failed to get message status after maximum retries", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ReceiptResponse getReceiptByTrackId(String trackId, String token) {
        String receiptUrl = apiDomain + "api/v1/Receipt/" + trackId;

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        int maxRetries = 3;  // Número máximo de intentos
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                RestTemplate client = new RestTemplate();

                ResponseEntity<byte[]> response = client.exchange(
                        receiptUrl,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        byte[].class
                );

                // Verificar si la respuesta fue exitosa
                if (!response.getStatusCode().is2xxSuccessful()) {
                    throw new EmailSendException("Failed to get receipt: " + response.getStatusCode(), HttpStatus.BAD_REQUEST);
                }

                // Crear y devolver la respuesta del recibo
                return ReceiptResponse.builder()
                        .content(new String(response.getBody(), StandardCharsets.ISO_8859_1))
                        .mediaType("application/zip")
                        .build();

            } catch (EmailSendException e) {
                log.error("Error getting message status", e);
                throw e;
            } catch (Exception e) {
                attempt++;
                log.error("Attempt " + attempt + " failed. Retrying...", e);
                if (attempt >= maxRetries) {
                    log.error("Max retries reached. Failing.");
                    throw new EmailSendException("Failed to get message status after retries", e, HttpStatus.INTERNAL_SERVER_ERROR);
                }
                try {
                    Thread.sleep(1000 * attempt); // Retraso exponencial: 1 segundo en el primer intento, 2 segundos en el segundo, etc.
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new EmailSendException("Interrupted during retry delay", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }
        throw new EmailSendException("Failed to get receipt after maximum retries", HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private EmailResponseDto convertToEmailResponseDto(RPostResponse rPostResponse) {
        EmailResponseDto responseDto = new EmailResponseDto();
        DataBodyDto dataDto = new DataBodyDto();
        AttributesDto attributesDto = new AttributesDto();

        dataDto.setType("email");
        dataDto.setID(rPostResponse.getResultContent().getTrackingId());

        attributesDto.setStatus(rPostResponse.getStatus());
        attributesDto.setStatusCode(Long.parseLong(rPostResponse.getStatusCode()));
        attributesDto.setStatusText(rPostResponse.getStatusText());
        attributesDto.setMessages(rPostResponse.getMessage());
        attributesDto.setTrackingID(rPostResponse.getResultContent().getTrackingId());

        dataDto.setAttributes(attributesDto);
        responseDto.setData(dataDto);

        return responseDto;
    }


}
