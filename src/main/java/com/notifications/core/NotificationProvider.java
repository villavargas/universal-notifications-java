package com.notifications.core;

import java.util.concurrent.CompletableFuture;

/**
 * Core interface for notification providers.
 * Each provider (SendGrid, Twilio, Firebase, etc.) implements this interface.
 * 
 * Strategy Pattern: Each provider is a different strategy for sending notifications.
 */
public interface NotificationProvider {
    
    /**
     * Sends a notification synchronously
     * 
     * @param notification the notification to send
     * @return result of the send operation
     * @throws NotificationException if sending fails
     */
    NotificationResult send(Notification notification) throws NotificationException;
    
    /**
     * Sends a notification asynchronously
     * 
     * @param notification the notification to send
     * @return CompletableFuture with the result
     */
    CompletableFuture<NotificationResult> sendAsync(Notification notification);
    
    /**
     * Gets the channel this provider handles
     * 
     * @return the notification channel
     */
    NotificationChannel getChannel();
    
    /**
     * Gets the provider name (e.g., "SendGrid", "Twilio", "Firebase")
     * 
     * @return provider name
     */
    String getProviderName();
    
    /**
     * Checks if the provider is properly configured and ready to send
     * 
     * @return true if ready, false otherwise
     */
    boolean isConfigured();
    
    /**
     * Validates a notification before sending
     * 
     * @param notification the notification to validate
     * @throws ValidationException if validation fails
     */
    void validate(Notification notification) throws ValidationException;
}
