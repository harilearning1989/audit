package com.web.audit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.ConnectException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConnectException.class)
    public ProblemDetail handleConnectException(ConnectException ex) {
        // Create a ProblemDetail object
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Unable to connect to the service. Please try again later."
        );

        // Add additional information (optional)
        problemDetail.setTitle("Service Unavailable");
        problemDetail.setProperty("timestamp", System.currentTimeMillis());
        problemDetail.setProperty("exception", ex.getClass().getSimpleName());

        return problemDetail;
    }
}

