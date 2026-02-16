package com.notifications.core;

/**
 * Base exception for all notification-related errors
 */
public class NotificationException extends Exception {
    
    private final ErrorType errorType;
    
    public NotificationException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }
    
    public NotificationException(String message, Throwable cause, ErrorType errorType) {
        super(message, cause);
        this.errorType = errorType;
    }
    
    // Convenience constructor - defaults to UNKNOWN_ERROR
    public NotificationException(String message) {
        this(message, ErrorType.UNKNOWN_ERROR);
    }
    
    // Convenience constructor - defaults to UNKNOWN_ERROR
    public NotificationException(String message, Throwable cause) {
        this(message, cause, ErrorType.UNKNOWN_ERROR);
    }
    
    public ErrorType getErrorType() {
        return errorType;
    }
    
    /**
     * Types of errors that can occur
     */
    public enum ErrorType {
        VALIDATION_ERROR,
        CONFIGURATION_ERROR,
        PROVIDER_ERROR,
        NETWORK_ERROR,
        RATE_LIMIT_ERROR,
        UNKNOWN_ERROR
    }
}
