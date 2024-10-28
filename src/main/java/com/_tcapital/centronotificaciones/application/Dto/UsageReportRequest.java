package com._tcapital.centronotificaciones.application.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageReportRequest {
    @NotNull(message = "senderType cannot be null")
    private String senderType;

    @NotNull(message = "senderTypeValue cannot be null")
    private String senderTypeValue;

    @NotNull(message = "fromDate cannot be null")
    private String fromDate;

    @NotNull(message = "toDate cannot be null")
    private String toDate;

    @NotNull(message = "dateRangeType cannot be null")
    private String dateRangeType;

    @NotNull(message = "deliveryStatusType cannot be null")
    private String deliveryStatusType;

    @NotNull(message = "reportOutputType cannot be null")
    private String reportOutputType;
    private String serviceFeature;
    private String messageId;
    private String recipientAddress;
    private String recipientDomain;
}