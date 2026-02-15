package com.notifications.core;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Represents the result of a notification send operation.
 * Contains information about success/failure and relevant details.
 */
@Getter
@Builder
public class NotificationResult {
    
    /**
     * ID of the original notification
     */
    private final String notificationId;
    
    /**
     * Whether the notification was sent successfully
     */
    private final boolean success;
    
    /**
     * Channel through which the notification was sent
     */
    private final NotificationChannel channel;
    
    /**
     * Provider-specific message ID or tracking ID
     * Useful for tracking the notification in the provider's system
     */
    private final String providerId;
    
    /**
     * Human-readable message about the result
     */
    private final String message;
    
    /**
     * Error details if the notification failed
     */
    private final String errorDetails;
    
    /**
     * Timestamp when the send operation completed
     */
    @Builder.Default
    private final Instant timestamp = Instant.now();
    
    /**
     * Additional metadata from the provider response
     */
    private final Object metadata;
    
    /**
     * Creates a successful result
     */
    public static NotificationResult success(String notificationId, NotificationChannel channel, String providerId) {
        return NotificationResult.builder()
                .notificationId(notificationId)
                .success(true)
                .channel(channel)
                .providerId(providerId)
                .message("Notification sent successfully")
                .build();
    }
    
    /**
     * Creates a failed result
     */
    public static NotificationResult failure(String notificationId, NotificationChannel channel, String errorDetails) {
        return NotificationResult.builder()
                .notificationId(notificationId)
                .success(false)
                .channel(channel)
                .message("Failed to send notification")
                .errorDetails(errorDetails)
                .build();
    }
}
