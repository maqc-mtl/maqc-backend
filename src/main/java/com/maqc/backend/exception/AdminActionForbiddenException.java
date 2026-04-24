package com.maqc.backend.exception;

import lombok.Getter;

@Getter
public class AdminActionForbiddenException extends RuntimeException {
    private final String errorCode;

    public AdminActionForbiddenException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
