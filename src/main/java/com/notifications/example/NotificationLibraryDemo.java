package com.notifications.example;

import com.notifications.config.NotificationConfig;
import com.notifications.config.ProviderConfig;
import com.notifications.core.*;
import com.notifications.factory.NotificationServiceFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Demo application showing how to use the Notification Library.
 * 
 * This demonstrates:
 * - Service configuration and initialization
 * - Sending notifications via different channels (Email, SMS, Push)
 * - Synchronous and asynchronous sending
 * - Batch sending
 * - Error handling
 * - Custom metadata
 */
@Slf4j
public class NotificationLibraryDemo {

    public static void main(String[] args) {
        log.info("=== Notification Library Demo ===\n");

        // Step 1: Configure the notification service
        NotificationService service = configureService();

        // Step 2: Send individual notifications
        sendEmailExample(service);
        sendSmsExample(service);
        sendPushExample(service);

        // Step 3: Send notifications asynchronously
        sendAsyncExample(service);

        // Step 4: Send batch notifications
        sendBatchExample(service);

        // Step 5: Handle errors
        errorHandlingExample(service);

        // Step 6: Check provider health
        healthCheckExample(service);

        log.info("\n=== Demo Complete ===");
    }

    /**
     * Configure the notification service with all providers
     */
    private static NotificationService configureService() {
        log.info("Configuring Notification Service...");

        // Create provider configurations
        Map<NotificationChannel, ProviderConfig> providers = Map.of(
            // Email Provider (SendGrid)
            NotificationChannel.EMAIL, ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("SG.your-sendgrid-api-key")
                .from("noreply@yourcompany.com")
                .endpoint("https://api.sendgrid.com/v3/mail/send")
                .enabled(true)
                .property("fromName", "Your Company")
                .build(),

            // SMS Provider (Twilio)
            NotificationChannel.SMS, ProviderConfig.builder()
                .channel(NotificationChannel.SMS)
                .providerName("Twilio")
                .apiKey("ACxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
                .apiSecret("your-auth-token")
                .from("+15551234567")
                .endpoint("https://api.twilio.com/2010-04-01")
                .enabled(true)
                .build(),

            // Push Provider (Firebase)
            NotificationChannel.PUSH, ProviderConfig.builder()
                .channel(NotificationChannel.PUSH)
                .providerName("Firebase")
                .apiKey("AAAAxxxxxxx:APAxxxxxxxxxxxxxxxxxxxxxxx")
                .endpoint("https://fcm.googleapis.com/fcm/send")
                .enabled(true)
                .property("projectId", "your-firebase-project")
                .property("senderId", "123456789012")
                .build()
        );

        // Create service configuration
        NotificationConfig config = NotificationConfig.builder()
            .providers(providers)
            .asyncByDefault(false)
            .autoRetry(true)
            .build();

        // Create the service
        NotificationService service = NotificationServiceFactory.create(config);
        log.info("✓ Service configured with {} providers\n", providers.size());

        return service;
    }

    /**
     * Example: Send an email notification
     */
    private static void sendEmailExample(NotificationService service) {
        log.info("--- Email Example ---");

        try {
            Notification email = Notification.builder()
                .channel(NotificationChannel.EMAIL)
                .recipient("user@example.com")
                .subject("Welcome to Our Service!")
                .body("<h1>Welcome!</h1><p>Thank you for signing up.</p>")
                .priority(Notification.Priority.NORMAL)
                .metadataEntry("cc", "manager@example.com")
                .metadataEntry("template", "welcome-email")
                .build();

            NotificationResult result = service.send(email);

            if (result.isSuccess()) {
                log.info("✓ Email sent successfully!");
                log.info("  Provider ID: {}", result.getProviderId());
            }
        } catch (NotificationException e) {
            log.error("✗ Failed to send email: {}", e.getMessage());
        }
        log.info("");
    }

    /**
     * Example: Send an SMS notification
     */
    private static void sendSmsExample(NotificationService service) {
        log.info("--- SMS Example ---");

        try {
            Notification sms = Notification.builder()
                .channel(NotificationChannel.SMS)
                .recipient("+15559876543")
                .body("Your verification code is: 123456. Valid for 10 minutes.")
                .priority(Notification.Priority.HIGH)
                .metadataEntry("type", "verification")
                .build();

            NotificationResult result = service.send(sms);

            if (result.isSuccess()) {
                log.info("✓ SMS sent successfully!");
                log.info("  Provider ID: {}", result.getProviderId());
            }
        } catch (NotificationException e) {
            log.error("✗ Failed to send SMS: {}", e.getMessage());
        }
        log.info("");
    }

    /**
     * Example: Send a push notification
     */
    private static void sendPushExample(NotificationService service) {
        log.info("--- Push Notification Example ---");

        try {
            Notification push = Notification.builder()
                .channel(NotificationChannel.PUSH)
                .recipient("device-token-abc123xyz")
                .subject("New Message")
                .body("You have a new message from John Doe")
                .priority(Notification.Priority.URGENT)
                .metadataEntry("badge", 1)
                .metadataEntry("sound", "default")
                .metadataEntry("action", "VIEW_MESSAGE")
                .build();

            NotificationResult result = service.send(push);

            if (result.isSuccess()) {
                log.info("✓ Push notification sent successfully!");
                log.info("  Provider ID: {}", result.getProviderId());
            }
        } catch (NotificationException e) {
            log.error("✗ Failed to send push notification: {}", e.getMessage());
        }
        log.info("");
    }

    /**
     * Example: Send notification asynchronously
     */
    private static void sendAsyncExample(NotificationService service) {
        log.info("--- Async Example ---");

        Notification email = Notification.builder()
            .channel(NotificationChannel.EMAIL)
            .recipient("async@example.com")
            .subject("Async Email")
            .body("This email was sent asynchronously")
            .priority(Notification.Priority.NORMAL)
            .build();

        CompletableFuture<NotificationResult> future = service.sendAsync(email);

        future.thenAccept(result -> {
            if (result.isSuccess()) {
                log.info("✓ Async email sent successfully!");
            } else {
                log.error("✗ Async email failed: {}", result.getMessage());
            }
        }).exceptionally(throwable -> {
            log.error("✗ Async operation failed: {}", throwable.getMessage());
            return null;
        });

        // Wait for completion (in real app, you wouldn't block like this)
        try {
            future.get();
        } catch (Exception e) {
            log.error("Error waiting for async result", e);
        }
        log.info("");
    }

    /**
     * Example: Send notifications in batch
     */
    private static void sendBatchExample(NotificationService service) {
        log.info("--- Batch Example ---");

        List<Notification> batch = List.of(
            Notification.builder()
                .channel(NotificationChannel.EMAIL)
                .recipient("user1@example.com")
                .subject("Batch Email 1")
                .body("Content for user 1")
                .build(),
            Notification.builder()
                .channel(NotificationChannel.EMAIL)
                .recipient("user2@example.com")
                .subject("Batch Email 2")
                .body("Content for user 2")
                .build(),
            Notification.builder()
                .channel(NotificationChannel.SMS)
                .recipient("+15551111111")
                .body("Batch SMS message")
                .build()
        );

        CompletableFuture<List<NotificationResult>> future = service.sendBatch(batch);

        try {
            List<NotificationResult> results = future.get();
            long successCount = results.stream().filter(NotificationResult::isSuccess).count();
            log.info("✓ Batch sent: {}/{} successful", successCount, results.size());
        } catch (Exception e) {
            log.error("✗ Batch send failed: {}", e.getMessage());
        }
        log.info("");
    }

    /**
     * Example: Error handling
     */
    private static void errorHandlingExample(NotificationService service) {
        log.info("--- Error Handling Example ---");

        // Try to send with invalid data
        try {
            Notification invalid = Notification.builder()
                .channel(NotificationChannel.EMAIL)
                .recipient("invalid-email")  // Invalid email format
                .subject("Test")
                .body("Test")
                .build();

            service.send(invalid);
        } catch (ValidationException e) {
            log.info("✓ Validation error caught correctly: {}", e.getMessage());
        } catch (NotificationException e) {
            log.error("✗ Unexpected error: {}", e.getMessage());
        }
        log.info("");
    }

    /**
     * Example: Health check
     */
    private static void healthCheckExample(NotificationService service) {
        log.info("--- Health Check Example ---");

        boolean emailHealthy = service.isChannelSupported(NotificationChannel.EMAIL);
        boolean smsHealthy = service.isChannelSupported(NotificationChannel.SMS);
        boolean pushHealthy = service.isChannelSupported(NotificationChannel.PUSH);

        log.info("Email provider: {}", emailHealthy ? "✓ Healthy" : "✗ Unavailable");
        log.info("SMS provider: {}", smsHealthy ? "✓ Healthy" : "✗ Unavailable");
        log.info("Push provider: {}", pushHealthy ? "✓ Healthy" : "✗ Unavailable");
    }
}
