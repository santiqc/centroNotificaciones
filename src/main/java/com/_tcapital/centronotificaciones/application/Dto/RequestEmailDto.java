package com._tcapital.centronotificaciones.application.Dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RequestEmailDto {

    @Schema(description = "Sender's email address", example = "user@example.com")
    private String from;

    @Schema(description = "Recipient's email address", example = "")
    private String to;

    @Schema(description = "CC recipient email addresses", example = "")
    private String cc;

    @Schema(description = "BCC recipient email addresses", example = "")
    private String bcc;

    @Schema(description = "Subject of the email", example = "")
    private String subject;

    @Schema(description = "Body of the email", example = "")
    private String body;

    @Schema(description = "List of file attachments", example = "[]")
    private List<String> attachments;

    @Schema(description = "Flag indicating if the email is large", example = "false")
    private Boolean isLargeMail;

    @Schema(description = "Name of the application", example = "")
    private String nameApplication;
}