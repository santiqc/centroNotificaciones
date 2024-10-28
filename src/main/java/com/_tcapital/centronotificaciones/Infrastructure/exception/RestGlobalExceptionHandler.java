package com._tcapital.centronotificaciones.Infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestGlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        String detailedMessage = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
        ErrorResponse errorResponse = new ErrorResponse(
                "Error in operation",
                detailedMessage,
                ex.getStatus().value() + ""
        );
        return new ResponseEntity<>(errorResponse, ex.getStatus());
    }

    // Manejador para excepciones no controladas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "Error interno del servidor",
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value() + ""
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}