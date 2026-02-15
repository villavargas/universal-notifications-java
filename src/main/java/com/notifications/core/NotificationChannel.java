package com.notifications.core;

/**
 * Enum representing the different notification channels supported by the library.
 * Each channel has different characteristics and requirements.
 */
public enum NotificationChannel {
    /**
     * Email notifications - requires recipient email, subject, and body
     */
    EMAIL,
    
    /**
     * SMS notifications - requires phone number and message
     */
    SMS,
    
    /**
     * Push notifications - requires device token and message
     */
    PUSH,
    
    /**
     * Slack notifications - requires webhook URL or channel and message
     */
    SLACK
}
