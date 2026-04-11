package com.maqc.backend.exception;

public class InvalidResetTokenException extends RuntimeException {
    private final String errorCode;

    public InvalidResetTokenException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
