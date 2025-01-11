package com.iris.software.jiracompliance.inbound.web.advice;

import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(SchedulerException.class)
    public ResponseEntity<String> handleSchedulerException(Exception e) {
        log.error("Error triggering Job: ", e);
        return ResponseEntity.internalServerError().body("Error triggering Job: " + e.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<String> handleConfigurationMissingException(Exception e) {
        log.error("Error loading validation rules from Excel file: ", e);
        return ResponseEntity.internalServerError().body("Error loading validation rules from Excel file: " + e.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(Exception e) {
        log.error("API not found Exception: ", e);
        return ResponseEntity.internalServerError().body("API not found: " + e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        log.error("Error while invoking JIRA Api", e);
        return ResponseEntity.internalServerError().body("Application error occurred: " + e.getMessage());
    }
}
