package com.notifications.core;

/**
 * Exception thrown when validation of notification data fails
 */
public class ValidationException extends NotificationException {
    
    public ValidationException(String message) {
        super(message, ErrorType.VALIDATION_ERROR);
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause, ErrorType.VALIDATION_ERROR);
    }
}
