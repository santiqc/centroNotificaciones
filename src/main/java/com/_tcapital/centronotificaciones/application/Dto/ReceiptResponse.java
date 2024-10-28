package com._tcapital.centronotificaciones.application.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReceiptResponse {
    private String content;
    private String mediaType;
}