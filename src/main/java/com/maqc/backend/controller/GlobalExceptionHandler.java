package com.maqc.backend.controller;

import com.maqc.backend.exception.AdminActionForbiddenException;
import com.maqc.backend.exception.ExpiredResetTokenException;
import com.maqc.backend.exception.InvalidCredentialsException;
import com.maqc.backend.exception.InvalidResetTokenException;
import com.maqc.backend.exception.PlanLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PlanLimitExceededException.class)
    public ResponseEntity<Map<String, String>> handlePlanLimitExceededException(PlanLimitExceededException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("errorCode", "PLAN_LIMIT_EXCEEDED");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());

        // Check if exception has an error code (custom exceptions)
        if (ex instanceof InvalidCredentialsException) {
            response.put("errorCode", ((InvalidCredentialsException) ex).getErrorCode());
        } else if (ex instanceof InvalidResetTokenException) {
            response.put("errorCode", ((InvalidResetTokenException) ex).getErrorCode());
        } else if (ex instanceof ExpiredResetTokenException) {
            response.put("errorCode", ((ExpiredResetTokenException) ex).getErrorCode());
        }

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AdminActionForbiddenException.class)
    public ResponseEntity<Map<String, String>> handleAdminActionForbiddenException(AdminActionForbiddenException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("errorCode", ex.getErrorCode());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
}
