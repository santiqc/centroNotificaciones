package com._tcapital.centronotificaciones.Infrastructure.exception;

import lombok.Data;

@Data
public class ErrorResponse {
    private String error;
    private String message;
    private String status;

    public ErrorResponse(String error, String message, String status) {
        this.error = error;
        this.message = message;
        this.status = status;
    }
}