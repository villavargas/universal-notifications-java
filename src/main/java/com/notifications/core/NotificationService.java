package com.notifications.core;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Main service interface for sending notifications.
 * This is the primary entry point for library users.
 * 
 * Facade Pattern: Simplifies the complexity of multiple providers and channels.
 */
public interface NotificationService {
    
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
     * Sends multiple notifications in batch (asynchronously)
     * 
     * @param notifications list of notifications to send
     * @return CompletableFuture with list of results
     */
    CompletableFuture<List<NotificationResult>> sendBatch(List<Notification> notifications);
    
    /**
     * Gets the provider for a specific channel
     * 
     * @param channel the notification channel
     * @return the provider handling that channel
     */
    NotificationProvider getProvider(NotificationChannel channel);
    
    /**
     * Checks if a channel is supported
     * 
     * @param channel the notification channel
     * @return true if supported, false otherwise
     */
    boolean isChannelSupported(NotificationChannel channel);
}
