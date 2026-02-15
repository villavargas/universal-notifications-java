package com.notifications.core;

/**
 * Exception thrown when a provider fails to send a notification
 */
public class ProviderException extends NotificationException {
    
    public ProviderException(String message) {
        super(message, ErrorType.PROVIDER_ERROR);
    }
    
    public ProviderException(String message, Throwable cause) {
        super(message, cause, ErrorType.PROVIDER_ERROR);
    }
    
    public ProviderException(String message, ErrorType errorType) {
        super(message, errorType);
    }
}
