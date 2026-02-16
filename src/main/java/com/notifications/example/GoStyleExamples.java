package com.notifications.example;

import com.notifications.config.ProviderConfig;
import com.notifications.core.Notify;
import com.notifications.core.NotificationChannel;
import com.notifications.core.NotificationException;
import com.notifications.core.NotificationResult;
import com.notifications.notifier.EmailNotifier;
import com.notifications.notifier.PushNotifier;
import com.notifications.notifier.SmsNotifier;
import lombok.extern.slf4j.Slf4j;

/**
 * Examples demonstrating the new Go-style API for the Notifications Library.
 * 
 * This new API is inspired by the Go "notify" library and provides:
 * - Simple interface: Notifier with just Send(subject, message)
 * - Easy composition: Use multiple notifiers together
 * - Transparent channel switching: All notifiers have the same interface
 * - Fluent configuration: Build and configure in one flow
 */
@Slf4j
public class GoStyleExamples {
    
    public static void main(String[] args) {
        log.info("=== Go-Style Notification API Examples ===\n");
        
        try {
            example1_SimpleUsage();
            example2_MultipleChannels();
            example3_MultipleProvidersPerChannel();
            example4_AsyncNotifications();
            example5_DisabledNotifier();
            
            log.info("\n=== All Go-Style Examples Completed! ===");
            
        } catch (Exception e) {
            log.error("Example failed: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Example 1: Simple usage - Just like Go!
     * notify.Send(ctx, subject, message)
     */
    private static void example1_SimpleUsage() throws NotificationException {
        log.info("📧 Example 1: Simple Usage (Go-style)");
        log.info("--------------------------------------");
        
        // Create an email notifier (like Go's mail.New())
        EmailNotifier email = EmailNotifier.builder()
            .providerName("SendGrid")
            .senderAddress("noreply@company.com")
            .senderName("My Company")
            .addReceiver("user@example.com")
            .addReceiver("admin@example.com")
            .build();
        
        // Create Notify and use the service (like Go's UseServices())
        Notify notify = Notify.create().use(email);
        
        // Send - just like Go! notify.Send(ctx, subject, message)
        NotificationResult result = notify.send(
            "Welcome!",
            "This is a simple notification using Go-style API"
        );
        
        log.info("✅ Sent! Provider: {}", result.getProviderId());
        log.info("");
    }
    
    /**
     * Example 2: Multiple channels - Same interface for all!
     */
    private static void example2_MultipleChannels() throws NotificationException {
        log.info("📱 Example 2: Multiple Channels (Same Interface)");
        log.info("------------------------------------------------");
        
        // Email notifier
        EmailNotifier email = EmailNotifier.builder()
            .providerName("SendGrid")
            .senderAddress("alerts@company.com")
            .senderName("Alert System")
            .addReceiver("ops@example.com")
            .build();
        
        // SMS notifier
        SmsNotifier sms = SmsNotifier.builder()
            .providerName("Twilio")
            .fromPhoneNumber("+15551234567")
            .addReceiver("+15559876543")
            .build();
        
        // Push notifier
        PushNotifier push = PushNotifier.builder()
            .providerName("Firebase")
            .addReceiver("device-token-abc123")
            .build();
        
        // Use all services - Same interface!
        Notify notify = Notify.create()
            .use(email)
            .use(sms)
            .use(push);
        
        // Send to ALL channels with one call!
        notify.send(
            "System Alert",
            "Server is experiencing high load"
        );
        
        log.info("✅ Sent to all 3 channels!");
        log.info("");
    }
    
    /**
     * Example 3: Multiple providers for the same channel
     * This demonstrates the power of the Go approach!
     */
    private static void example3_MultipleProvidersPerChannel() throws NotificationException {
        log.info("📧 Example 3: Multiple Providers per Channel");
        log.info("---------------------------------------------");
        
        // Primary email provider: SendGrid
        EmailNotifier sendgrid = EmailNotifier.builder()
            .providerName("SendGrid")
            .senderAddress("primary@company.com")
            .senderName("Primary Sender")
            .addReceiver("customer@example.com")
            .build();
        
        // Backup email provider: Mailgun (simulated with another EmailNotifier)
        EmailNotifier mailgun = EmailNotifier.builder()
            .providerName("Mailgun")
            .senderAddress("backup@company.com")
            .senderName("Backup Sender")
            .addReceiver("customer@example.com")
            .build();
        
        // Use both! They will both receive the message
        Notify notify = Notify.create()
            .use(sendgrid)
            .use(mailgun);
        
        // Send - both providers will receive it
        notify.send(
            "Important Update",
            "This goes through both SendGrid and Mailgun"
        );
        
        log.info("✅ Sent via 2 email providers!");
        log.info("");
    }
    
    /**
     * Example 4: Async notifications
     */
    private static void example4_AsyncNotifications() throws Exception {
        log.info("⚡ Example 4: Async Notifications");
        log.info("----------------------------------");
        
        EmailNotifier email = EmailNotifier.builder()
            .providerName("SendGrid")
            .senderAddress("async@company.com")
            .senderName("Async Sender")
            .addReceiver("user@example.com")
            .build();
        
        Notify notify = Notify.create().use(email);
        
        // Send async
        log.info("Sending async...");
        notify.sendAsync("Async Subject", "This is sent asynchronously")
            .thenAccept(result -> 
                log.info("✅ Async completed! Provider: {}", result.getProviderId())
            )
            .join();  // Wait for demo purposes
        
        log.info("");
    }
    
    /**
     * Example 5: Disabled notifier - No-op pattern
     */
    private static void example5_DisabledNotifier() throws NotificationException {
        log.info("🚫 Example 5: Disabled Notifier");
        log.info("--------------------------------");
        
        EmailNotifier email = EmailNotifier.builder()
            .providerName("SendGrid")
            .senderAddress("test@company.com")
            .senderName("Test Sender")
            .addReceiver("user@example.com")
            .build();
        
        // Create disabled notifier
        Notify notify = Notify.createDisabled().use(email);
        
        // This won't actually send (disabled)
        log.info("Attempting to send (but notifier is disabled)...");
        NotificationResult result = notify.send("Test", "This won't be sent");
        
        log.info("✅ No-op completed (notifier was disabled)");
        log.info("");
    }
}
