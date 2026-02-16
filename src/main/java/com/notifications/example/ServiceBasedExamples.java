package com.notifications.example;

import com.notifications.core.Notify;
import com.notifications.core.Notifier;
import com.notifications.core.NotificationException;
import com.notifications.core.NotificationResult;
import com.notifications.service.email.SendGridNotifier;
import com.notifications.service.email.EmailNotifier;
import com.notifications.service.sms.TwilioNotifier;
import com.notifications.service.push.FcmNotifier;
import com.notifications.service.chat.SlackNotifier;
import lombok.extern.slf4j.Slf4j;

/**
 * Examples demonstrating the new service-based organization.
 * Shows how to use specific provider notifiers and compose them.
 */
@Slf4j
public class ServiceBasedExamples {
    
    public static void main(String[] args) {
        try {
            // Example 1: Using specific providers
            specificProviderExample();
            
            // Example 2: Multiple email providers simultaneously
            multipleEmailProvidersExample();
            
            // Example 3: Complete multi-channel notification
            multiChannelExample();
            
            // Example 4: Adding new channel without modifying core
            extensibilityExample();
            
        } catch (NotificationException e) {
            log.error("Example failed", e);
        }
    }
    
    /**
     * Example 1: Using specific provider notifiers
     */
    private static void specificProviderExample() throws NotificationException {
        log.info("\n=== Example 1: Specific Provider Notifiers ===");
        
        // SendGrid for transactional emails
        Notifier sendgrid = SendGridNotifier.builder()
                .apiKey("SG.xxxxx")
                .from("noreply@example.com")
                .fromName("MyApp")
                .addTo("user@example.com")
                .templateId("d-12345")
                .addTemplateData("name", "John")
                .addCategory("transactional")
                .build();
        
        NotificationResult result = sendgrid.send(
                "Welcome to MyApp", 
                "Thank you for signing up!"
        );
        
        log.info("SendGrid result: {}", result.getMessage());
    }
    
    /**
     * Example 2: Multiple email providers for redundancy
     */
    private static void multipleEmailProvidersExample() throws NotificationException {
        log.info("\n=== Example 2: Multiple Email Providers ===");
        
        // Primary: SendGrid
        Notifier sendgrid = SendGridNotifier.builder()
                .apiKey("sg-key")
                .from("primary@example.com")
                .fromName("MyApp")
                .addTo("user@example.com")
                .build();
        
        // Fallback: Generic SMTP
        Notifier smtp = EmailNotifier.builder()
                .smtpHost("smtp.gmail.com")
                .smtpPort(587)
                .from("fallback@example.com")
                .fromName("MyApp Backup")
                .addReceiver("user@example.com")
                .build();
        
        // Send via both providers in parallel
        Notify notify = Notify.create()
                .use(sendgrid)
                .use(smtp);
        
        NotificationResult result = notify.send(
                "Important Update", 
                "Your account has been updated"
        );
        
        log.info("Multi-provider result: {}", result.getMessage());
        log.info("Individual results: {}", result.getIndividualResults().size());
    }
    
    /**
     * Example 3: Complete multi-channel notification system
     */
    private static void multiChannelExample() throws NotificationException {
        log.info("\n=== Example 3: Multi-Channel Notification ===");
        
        // Email via SendGrid
        Notifier email = SendGridNotifier.builder()
                .apiKey("sg-key")
                .from("alerts@example.com")
                .addTo("admin@example.com")
                .build();
        
        // SMS via Twilio
        Notifier sms = TwilioNotifier.builder()
                .accountSid("AC...")
                .authToken("...")
                .fromPhoneNumber("+15551234567")
                .addTo("+15559876543")
                .build();
        
        // Push via Firebase
        Notifier push = FcmNotifier.builder()
                .projectId("my-project")
                .addDeviceToken("device-token-123")
                .priority("high")
                .build();
        
        // Slack notification
        Notifier slack = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/...")
                .username("AlertBot")
                .iconEmoji(":rotating_light:")
                .build();
        
        // Compose all channels
        Notify notify = Notify.create()
                .use(email)
                .use(sms)
                .use(push)
                .use(slack);
        
        // Send to all channels in parallel
        NotificationResult result = notify.send(
                "🚨 CRITICAL ALERT",
                "Production database connection lost. Immediate action required."
        );
        
        log.info("Sent to {} channels", result.getIndividualResults().size());
        result.getIndividualResults().forEach(r -> 
                log.info("  - {}: {}", r.getProviderId(), r.getMessage())
        );
    }
    
    /**
     * Example 4: Extensibility - Adding WhatsApp without modifying core
     */
    private static void extensibilityExample() throws NotificationException {
        log.info("\n=== Example 4: Extensibility (WhatsApp) ===");
        
        // Create a custom WhatsApp notifier (simulated)
        Notifier whatsapp = new WhatsAppNotifierSimulation();
        
        // Use it immediately alongside existing notifiers
        Notifier email = SendGridNotifier.builder()
                .apiKey("sg-key")
                .from("alerts@example.com")
                .addTo("user@example.com")
                .build();
        
        Notify notify = Notify.create()
                .use(email)
                .use(whatsapp);  // ✅ Works without modifying core!
        
        NotificationResult result = notify.send(
                "Order Shipped",
                "Your order #12345 has been shipped and is on its way!"
        );
        
        log.info("Multi-channel result (including WhatsApp): {}", result.getMessage());
    }
    
    /**
     * Simulated WhatsApp notifier to demonstrate extensibility.
     * In a real implementation, this would be in service/messaging/WhatsAppNotifier.java
     */
    private static class WhatsAppNotifierSimulation implements Notifier {
        @Override
        public NotificationResult send(String subject, String message) throws NotificationException {
            log.info("Simulated WhatsApp send: subject='{}', messageLength={}", 
                    subject, message != null ? message.length() : 0);
            
            // Simulate API call
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            return NotificationResult.builder()
                    .success(true)
                    .providerId("whatsapp-simulation-001")
                    .message("WhatsApp message sent to 1 contact")
                    .build();
        }
    }
}
