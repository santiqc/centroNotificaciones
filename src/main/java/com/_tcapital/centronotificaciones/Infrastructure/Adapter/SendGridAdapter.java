package com._tcapital.centronotificaciones.Infrastructure.Adapter;

import com._tcapital.centronotificaciones.Infrastructure.exception.EmailSendException;
import com._tcapital.centronotificaciones.application.Dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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

        } catch (Exception e) {
            log.error("Error sending email through RPost", e);
            throw new EmailSendException("Error sending email through RPost " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

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

    private String processEmailList(String emails) {
        if (emails == null || emails.trim().isEmpty()) {
            return "";
        }
        return Arrays.stream(emails.split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(";"));
    }
}
