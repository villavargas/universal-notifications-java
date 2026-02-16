package com.notifications.example;

import com.notifications.core.Notify;
import com.notifications.core.NotificationResult;
import com.notifications.core.Notifier;
import com.notifications.notifier.EmailNotifier;
import com.notifications.notifier.PushNotifier;
import com.notifications.notifier.SmsNotifier;
import lombok.extern.slf4j.Slf4j;

/**
 * Examples demonstrating the Go-style simple interface.
 * 
 * This shows how the new Notifier interface works:
 * - Simple send(subject, message) method
 * - Transparent channel switching
 * - Easy composition of multiple notifiers
 * - Minimal code required
 */
@Slf4j
public class SimpleNotifierExamples {
    
    public static void main(String[] args) {
        log.info("=== Simple Notifier Examples (Go-style API) ===\n");
        
        try {
            example1_SingleNotifier();
            example2_MultipleNotifiers();
            example3_ChannelAgnostic();
            example4_DisableEnable();
            example5_CompositeResults();
            
            log.info("\n=== All Simple Notifier Examples Completed! ===");
            
        } catch (Exception e) {
            log.error("Example failed", e);
        }
    }
    
    /**
     * Example 1: Using a single notifier
     */
    private static void example1_SingleNotifier() throws Exception {
        log.info("📧 Example 1: Single Email Notifier");
        log.info("-----------------------------------");
        
        // Create an email notifier (similar to Go's mail.New())
        Notifier emailNotifier = EmailNotifier.builder()
                .providerName("SendGrid")
                .senderAddress("noreply@example.com")
                .senderName("My App")
                .addReceiver("user@example.com")
                .build();
        
        // Send with simple interface
        NotificationResult result = emailNotifier.send(
                "Welcome to My App",
                "<h1>Hello!</h1><p>Thanks for signing up!</p>"
        );
        
        log.info("✅ {}", result.getMessage());
        log.info("");
    }
    
    /**
     * Example 2: Using multiple notifiers with Notify (like Go's notify.UseServices())
     */
    private static void example2_MultipleNotifiers() throws Exception {
        log.info("📱 Example 2: Multiple Notifiers with Notify");
        log.info("--------------------------------------------");
        
        // Create individual notifiers
        Notifier emailNotifier = EmailNotifier.builder()
                .providerName("SendGrid")
                .senderAddress("noreply@example.com")
                .addReceiver("user@example.com")
                .build();
        
        Notifier smsNotifier = SmsNotifier.builder()
                .providerName("Twilio")
                .fromPhoneNumber("+15551234567")
                .addReceiver("+15559876543")
                .build();
        
        Notifier pushNotifier = PushNotifier.builder()
                .providerName("Firebase")
                .addReceiver("device-token-abc123")
                .build();
        
        // Compose them (similar to Go's notify.UseServices())
        Notify notify = Notify.create()
                .use(emailNotifier)
                .use(smsNotifier)
                .use(pushNotifier);
        
        // Send to ALL channels with one call!
        NotificationResult result = notify.send(
                "Critical Alert",
                "Your server is experiencing high load!"
        );
        
        log.info("✅ {}", result.getMessage());
        
        // Check individual results
        if (result.isComposite()) {
            log.info("Individual results:");
            for (NotificationResult individual : result.getIndividualResults()) {
                log.info("  - {} via {}: {}", 
                        individual.getChannel(),
                        individual.getProviderId(),
                        individual.getMessage());
            }
        }
        log.info("");
    }
    
    /**
     * Example 3: Channel-agnostic code (works with any Notifier)
     */
    private static void example3_ChannelAgnostic() throws Exception {
        log.info("🔄 Example 3: Channel-Agnostic Code");
        log.info("-----------------------------------");
        
        // This method doesn't care what kind of notifier it is!
        sendNotification(
                EmailNotifier.builder()
                        .providerName("Mailgun")
                        .senderAddress("alerts@example.com")
                        .addReceiver("admin@example.com")
                        .build(),
                "Test Email",
                "This is via email"
        );
        
        sendNotification(
                SmsNotifier.builder()
                        .providerName("Plivo")
                        .fromPhoneNumber("+15551111111")
                        .addReceiver("+15552222222")
                        .build(),
                "Test SMS",
                "This is via SMS"
        );
        
        sendNotification(
                PushNotifier.builder()
                        .providerName("APNs")
                        .addReceiver("ios-device-token")
                        .build(),
                "Test Push",
                "This is via push"
        );
        
        log.info("");
    }
    
    /**
     * Channel-agnostic method that works with ANY Notifier implementation
     */
    private static void sendNotification(Notifier notifier, String subject, String message) throws Exception {
        // This code doesn't know or care what type of notifier it is!
        NotificationResult result = notifier.send(subject, message);
        log.info("✅ Sent via {}: {}", 
                notifier.getClass().getSimpleName(), 
                result.getMessage());
    }
    
    /**
     * Example 4: Disable/Enable functionality
     */
    private static void example4_DisableEnable() throws Exception {
        log.info("🔕 Example 4: Disable/Enable");
        log.info("----------------------------");
        
        Notify notify = Notify.create()
                .use(EmailNotifier.builder()
                        .senderAddress("test@example.com")
                        .addReceiver("user@example.com")
                        .build());
        
        // Send normally
        NotificationResult result1 = notify.send("Test 1", "This should send");
        log.info("✅ Enabled: {}", result1.getMessage());
        
        // Disable and try to send
        notify.disable();
        NotificationResult result2 = notify.send("Test 2", "This won't send");
        log.info("🔕 Disabled: {}", result2.getMessage());
        
        // Re-enable
        notify.enable();
        NotificationResult result3 = notify.send("Test 3", "This should send again");
        log.info("✅ Re-enabled: {}", result3.getMessage());
        
        log.info("");
    }
    
    /**
     * Example 5: Multiple providers for the same channel (redundancy/failover)
     */
    private static void example5_CompositeResults() throws Exception {
        log.info("📊 Example 5: Multiple Email Providers (Redundancy)");
        log.info("---------------------------------------------------");
        
        // Create multiple email providers for redundancy
        Notifier sendgridEmail = EmailNotifier.builder()
                .providerName("SendGrid")
                .senderAddress("noreply@example.com")
                .addReceiver("customer@example.com")
                .build();
        
        Notifier mailgunEmail = EmailNotifier.builder()
                .providerName("Mailgun")
                .senderAddress("noreply@example.com")
                .addReceiver("customer@example.com")
                .build();
        
        Notifier awsSesEmail = EmailNotifier.builder()
                .providerName("AWS-SES")
                .senderAddress("noreply@example.com")
                .addReceiver("customer@example.com")
                .build();
        
        // Use all three email providers
        Notify notify = Notify.create()
                .use(sendgridEmail, mailgunEmail, awsSesEmail);
        
        log.info("Configured {} email providers for redundancy", notify.getNotifierCount());
        
        // Send via all providers
        NotificationResult result = notify.send(
                "Important Update",
                "This email is sent via 3 different providers for redundancy"
        );
        
        log.info("✅ {}", result.getMessage());
        log.info("Detailed results:");
        for (NotificationResult individual : result.getIndividualResults()) {
            log.info("  - Provider {}: {}", 
                    individual.getProviderId().split("-")[0].toUpperCase(),
                    individual.isSuccess() ? "SUCCESS" : "FAILED");
        }
        
        log.info("");
    }
}
