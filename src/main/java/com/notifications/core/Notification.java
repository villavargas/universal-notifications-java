package com.notifications.core;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a notification to be sent through any channel.
 * This is the unified model that works across all notification channels.
 * 
 * Uses Builder pattern for flexible construction.
 */
@Getter
@Builder
public class Notification {
    
    /**
     * Unique identifier for this notification
     */
    private final String id;
    
    /**
     * The channel through which this notification should be sent
     */
    private final NotificationChannel channel;
    
    /**
     * Primary recipient of the notification
     * Format depends on channel:
     * - EMAIL: email address
     * - SMS: phone number
     * - PUSH: device token
     * - SLACK: channel or webhook URL
     */
    private final String recipient;
    
    /**
     * Subject/title of the notification (mainly for EMAIL)
     * Optional for other channels
     */
    private final String subject;
    
    /**
     * Main content/body of the notification
     */
    private final String body;
    
    /**
     * Priority level of the notification
     */
    @Builder.Default
    private final Priority priority = Priority.NORMAL;
    
    /**
     * Additional metadata specific to the channel or provider
     * Examples:
     * - Email: "cc", "bcc", "attachments"
     * - SMS: "senderId"
     * - Push: "badge", "sound", "data"
     */
    @Singular("metadataEntry")
    private final Map<String, Object> metadata;
    
    /**
     * Timestamp when the notification was created
     */
    @Builder.Default
    private final Instant createdAt = Instant.now();
    
    /**
     * Priority levels for notifications
     */
    public enum Priority {
        LOW,
        NORMAL,
        HIGH,
        URGENT
    }
}
