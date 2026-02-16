package com.notifications.core;

/**
 * Enum representing the different notification channels supported by the library.
 * Each channel has different characteristics and requirements.
 * 
 * @deprecated This enum is kept for backward compatibility with the legacy NotificationProvider API.
 *             For new code, use specific Notifier implementations from the service package
 *             (e.g., {@link com.notifications.service.email.EmailNotifier},
 *             {@link com.notifications.service.sms.TwilioNotifier},
 *             {@link com.notifications.service.push.FcmNotifier},
 *             {@link com.notifications.service.chat.SlackNotifier}).
 *             The new approach does not require a channel enum and allows unlimited extensibility.
 * @see com.notifications.core.Notifier
 * @see com.notifications.core.Notify
 */
@Deprecated(since = "2.0.0", forRemoval = true)
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
