package com.notifications.core;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

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
     * For composite results: individual results from multiple notifiers
     */
    private final List<NotificationResult> individualResults;
    
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
    
    /**
     * Creates a composite result from multiple individual results.
     * Success is true only if ALL individual results succeeded.
     * 
     * @param results individual results from multiple notifiers
     * @return composite result
     */
    public static NotificationResult composite(List<NotificationResult> results) {
        boolean allSuccess = results.stream().allMatch(NotificationResult::isSuccess);
        long successCount = results.stream().filter(NotificationResult::isSuccess).count();
        
        return NotificationResult.builder()
                .success(allSuccess)
                .message(String.format("Sent to %d/%d notifiers", successCount, results.size()))
                .individualResults(new ArrayList<>(results))
                .build();
    }
    
    /**
     * Creates a result indicating the Notify instance was disabled.
     * 
     * @return disabled result
     */
    public static NotificationResult disabled() {
        return NotificationResult.builder()
                .success(true)
                .message("Notify instance is disabled, no notifications sent")
                .build();
    }
    
    /**
     * Creates a result indicating no notifiers were configured.
     * 
     * @return no notifiers result
     */
    public static NotificationResult noNotifiers() {
        return NotificationResult.builder()
                .success(false)
                .message("No notifiers configured")
                .build();
    }
    
    /**
     * Checks if this is a composite result.
     * 
     * @return true if composite, false otherwise
     */
    public boolean isComposite() {
        return individualResults != null && !individualResults.isEmpty();
    }
}
