package com.notifications.core;

/**
 * Notifier defines the behavior for notification services.
 * 
 * This is the core interface inspired by Go's "notify" library design.
 * It follows the principle of simplicity: one method to rule them all.
 * 
 * The Send method sends a subject and a message to the configured destination(s).
 * Different implementations handle channel-specific logic internally:
 * 
 * - Email: subject becomes email subject, message becomes body
 * - SMS: subject and message are concatenated (SMS has no subject concept)
 * - Push: subject becomes notification title, message becomes body
 * - Slack: subject and message are formatted together
 * 
 * This interface allows:
 * - Easy implementation of new notification channels
 * - Transparent switching between channels
 * - Composability: multiple Notifiers can be combined
 * - Simple mocking for testing
 * 
 * Example:
 * <pre>
 * Notifier emailNotifier = ...;
 * Notifier smsNotifier = ...;
 * 
 * // Both use the same interface
 * emailNotifier.send("Welcome", "Hello World!");  // Email with subject
 * smsNotifier.send("Alert", "System Down");       // SMS concatenates both
 * </pre>
 */
public interface Notifier {
    
    /**
     * Sends a notification with a subject and message.
     * 
     * How subject and message are used depends on the channel:
     * - Email: subject = email subject, message = email body
     * - SMS: subject + message concatenated (SMS has no subject)
     * - Push: subject = notification title, message = notification body
     * - Slack: subject + message formatted appropriately
     * 
     * @param subject the subject/title of the notification (may be null for channels that don't support it)
     * @param message the main content/body of the notification
     * @return result of the send operation
     * @throws NotificationException if sending fails
     */
    NotificationResult send(String subject, String message) throws NotificationException;
}
