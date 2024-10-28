package com._tcapital.centronotificaciones.Infrastructure.exception;

import org.springframework.http.HttpStatus;

public class EmailSendException extends BaseException {
    public EmailSendException(String message, HttpStatus status) {
        super(message, status);
    }

    public EmailSendException(String message, Throwable cause, HttpStatus status) {
        super(message, cause, status);
    }
}