package com.notifications.example;

import com.notifications.config.NotificationConfig;
import com.notifications.config.ProviderConfig;
import com.notifications.core.Notification;
import com.notifications.core.NotificationChannel;
import com.notifications.core.NotificationException;
import com.notifications.core.NotificationResult;
import com.notifications.factory.NotificationServiceFactory;
import com.notifications.core.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Executable examples demonstrating the Notifications Library.
 * This class can be run standalone to see the library in action.
 */
public class NotificationExamples {
    
    private static final Logger log = LoggerFactory.getLogger(NotificationExamples.class);
    
    public static void main(String[] args) {
        log.info("=== Notifications Library - Demo Examples ===\n");
        
        try {
            // Create notification service with all providers
            NotificationService service = createNotificationService();
            
            // Run examples
            runEmailExample(service);
            runSmsExample(service);
            runPushExample(service);
            runAsyncExample(service);
            runBatchExample(service);
            
            log.info("\n=== All Examples Completed Successfully! ===");
            
        } catch (Exception e) {
            log.error("Error running examples", e);
        }
    }
    
    /**
     * Creates and configures the NotificationService with all providers
     */
    private static NotificationService createNotificationService() {
        log.info("📦 Creating NotificationService with all providers...\n");
        
        // Configure Email Provider (SendGrid simulation)
        ProviderConfig emailConfig = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("SG.demo_api_key_xxxxxxxxxxxxx")
                .from("noreply@example.com")
                .endpoint("https://api.sendgrid.com/v3/mail/send")
                .enabled(true)
                .property("fromName", "Demo Company")
                .build();
        
        // Configure SMS Provider (Twilio simulation)
        ProviderConfig smsConfig = ProviderConfig.builder()
                .channel(NotificationChannel.SMS)
                .providerName("Twilio")
                .apiKey("ACxxxxxxxxxxxxxxxxxxxxxxxxxx")
                .apiSecret("demo_auth_token")
                .from("+15551234567")
                .endpoint("https://api.twilio.com/2010-04-01")
                .enabled(true)
                .build();
        
        // Configure Push Provider (Firebase simulation)
        ProviderConfig pushConfig = ProviderConfig.builder()
                .channel(NotificationChannel.PUSH)
                .providerName("Firebase")
                .apiKey("AAAAxxxxxxx:APAxxxxxxxxxxxxxxxxx")
                .endpoint("https://fcm.googleapis.com/fcm/send")
                .enabled(true)
                .property("projectId", "demo-project")
                .build();
        
        // Create configuration
        NotificationConfig config = NotificationConfig.builder()
                .providers(Map.of(
                        NotificationChannel.EMAIL, emailConfig,
                        NotificationChannel.SMS, smsConfig,
                        NotificationChannel.PUSH, pushConfig
                ))
                .build();
        
        // Create service using factory
        NotificationService service = NotificationServiceFactory.create(config);
        log.info("✅ NotificationService created successfully\n");
        
        return service;
    }
    
    /**
     * Example 1: Send an Email Notification
     */
    private static void runEmailExample(NotificationService service) {
        log.info("📧 Example 1: Sending Email Notification");
        log.info("----------------------------------------");
        
        try {
            Notification email = Notification.builder()
                    .channel(NotificationChannel.EMAIL)
                    .recipient("user@example.com")
                    .subject("Welcome to Notifications Library!")
                    .body("<h1>Welcome!</h1><p>This is a demo email from the Notifications Library.</p>")
                    .priority(Notification.Priority.NORMAL)
                    .metadataEntry("template", "welcome")
                    .metadataEntry("cc", "admin@example.com")
                    .build();
            
            NotificationResult result = service.send(email);
            
            printResult(result);
        } catch (NotificationException e) {
            log.error("Failed to send email: {}", e.getMessage());
        }
        log.info("");
    }
    
    /**
     * Example 2: Send an SMS Notification
     */
    private static void runSmsExample(NotificationService service) {
        log.info("📱 Example 2: Sending SMS Notification");
        log.info("--------------------------------------");
        
        try {
            Notification sms = Notification.builder()
                    .channel(NotificationChannel.SMS)
                    .recipient("+15559876543")
                    .body("Your verification code is: 123456. Valid for 5 minutes.")
                    .priority(Notification.Priority.HIGH)
                    .metadataEntry("type", "verification")
                    .build();
            
            NotificationResult result = service.send(sms);
            
            printResult(result);
        } catch (NotificationException e) {
            log.error("Failed to send SMS: {}", e.getMessage());
        }
        log.info("");
    }
    
    /**
     * Example 3: Send a Push Notification
     */
    private static void runPushExample(NotificationService service) {
        log.info("🔔 Example 3: Sending Push Notification");
        log.info("---------------------------------------");
        
        try {
            Notification push = Notification.builder()
                    .channel(NotificationChannel.PUSH)
                    .recipient("device-token-xxxxxxxxxxxxxxxxxxxxxxxxxxxx")
                    .subject("New Message")
                    .body("You have a new message from John!")
                    .priority(Notification.Priority.HIGH)
                    .metadataEntry("badge", "1")
                    .metadataEntry("sound", "default")
                    .build();
            
            NotificationResult result = service.send(push);
            
            printResult(result);
        } catch (NotificationException e) {
            log.error("Failed to send push notification: {}", e.getMessage());
        }
        log.info("");
    }
    
    /**
     * Example 4: Send Async Notification
     */
    private static void runAsyncExample(NotificationService service) {
        log.info("⚡ Example 4: Sending Async Notification");
        log.info("---------------------------------------");
        
        Notification email = Notification.builder()
                .channel(NotificationChannel.EMAIL)
                .recipient("async@example.com")
                .subject("Async Test")
                .body("This email was sent asynchronously!")
                .priority(Notification.Priority.NORMAL)
                .build();
        
        log.info("Sending notification asynchronously...");
        
        CompletableFuture<NotificationResult> future = service.sendAsync(email);
        
        // Wait for completion and handle result
        future.thenAccept(result -> {
            log.info("Async notification completed!");
            printResult(result);
        }).exceptionally(ex -> {
            log.error("Async notification failed: {}", ex.getMessage());
            return null;
        }).join(); // Wait for completion in this example
        
        log.info("");
    }
    
    /**
     * Example 5: Send Batch Notifications
     */
    private static void runBatchExample(NotificationService service) {
        log.info("📦 Example 5: Sending Batch Notifications");
        log.info("-----------------------------------------");
        
        List<Notification> notifications = List.of(
                Notification.builder()
                        .channel(NotificationChannel.EMAIL)
                        .recipient("user1@example.com")
                        .subject("Batch Email 1")
                        .body("First email in batch")
                        .build(),
                Notification.builder()
                        .channel(NotificationChannel.SMS)
                        .recipient("+15551111111")
                        .body("First SMS in batch")
                        .build(),
                Notification.builder()
                        .channel(NotificationChannel.PUSH)
                        .recipient("device-token-1111111111111111")
                        .subject("Batch Push")
                        .body("First push in batch")
                        .build()
        );
        
        log.info("Sending {} notifications in batch...", notifications.size());
        
        CompletableFuture<List<NotificationResult>> future = service.sendBatch(notifications);
        
        future.thenAccept(results -> {
            log.info("Batch completed! Results:");
            int successCount = 0;
            for (NotificationResult result : results) {
                if (result.isSuccess()) {
                    successCount++;
                }
                printResult(result);
            }
            log.info("Batch summary: {}/{} successful", successCount, results.size());
        }).exceptionally(ex -> {
            log.error("Batch failed: {}", ex.getMessage());
            return null;
        }).join(); // Wait for completion in this example
        
        log.info("");
    }
    
    /**
     * Helper method to print notification results
     */
    private static void printResult(NotificationResult result) {
        if (result.isSuccess()) {
            log.info("✅ SUCCESS - Channel: {} | Provider ID: {} | Timestamp: {}", 
                    result.getChannel(), 
                    result.getProviderId(),
                    result.getTimestamp());
        } else {
            log.error("❌ FAILED - Channel: {} | Error: {}", 
                    result.getChannel(), 
                    result.getErrorDetails());
        }
    }
}
