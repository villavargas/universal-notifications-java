package com.notifications.service.email;

import com.notifications.core.NotificationException;
import com.notifications.core.NotificationResult;
import com.notifications.core.Notifier;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * SendGrid-specific email notifier.
 * Provides SendGrid-specific features like templates, categories, and dynamic data.
 * 
 * Example usage:
 * <pre>
 * Notifier sendgrid = SendGridNotifier.builder()
 *     .apiKey("SG.xxxxx")
 *     .from("sender@example.com")
 *     .fromName("MyApp")
 *     .addTo("recipient@example.com")
 *     .templateId("d-12345")  // Optional: use template
 *     .addTemplateData("name", "John")
 *     .addCategory("newsletter")
 *     .build();
 * 
 * sendgrid.send("Subject", "Message");
 * </pre>
 */
@Slf4j
public class SendGridNotifier implements Notifier {
    
    private final String apiKey;
    private final String from;
    private final String fromName;
    private final List<String> toAddresses;
    private final List<String> ccAddresses;
    private final List<String> bccAddresses;
    private final String replyTo;
    private final String templateId;
    private final Map<String, String> templateData;
    private final List<String> categories;
    private final Map<String, String> customArgs;
    
    private SendGridNotifier(Builder builder) {
        this.apiKey = builder.apiKey;
        this.from = builder.from;
        this.fromName = builder.fromName;
        this.toAddresses = new ArrayList<>(builder.toAddresses);
        this.ccAddresses = new ArrayList<>(builder.ccAddresses);
        this.bccAddresses = new ArrayList<>(builder.bccAddresses);
        this.replyTo = builder.replyTo;
        this.templateId = builder.templateId;
        this.templateData = new HashMap<>(builder.templateData);
        this.categories = new ArrayList<>(builder.categories);
        this.customArgs = new HashMap<>(builder.customArgs);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Adds a recipient to this notifier.
     * 
     * @param email the email address to add
     * @return this notifier for chaining
     */
    public SendGridNotifier addTo(String email) {
        if (email != null && !email.trim().isEmpty()) {
            toAddresses.add(email.trim());
        }
        return this;
    }
    
    /**
     * Adds multiple recipients to this notifier.
     * 
     * @param emails the email addresses to add
     * @return this notifier for chaining
     */
    public SendGridNotifier addTo(String... emails) {
        for (String email : emails) {
            addTo(email);
        }
        return this;
    }
    
    @Override
    public NotificationResult send(String subject, String message) throws NotificationException {
        if (toAddresses.isEmpty()) {
            throw new NotificationException("No recipients configured for SendGrid notifier");
        }
        
        log.info("Sending email via SendGrid to {} recipients: subject='{}', template={}", 
                toAddresses.size(), subject, templateId != null ? templateId : "none");
        
        try {
            // Simulate SendGrid API call
            simulateSendGridSend(subject, message);
            
            String providerId = "sendgrid-" + UUID.randomUUID();
            
            log.info("Email sent successfully via SendGrid: providerId={}", providerId);
            
            return NotificationResult.builder()
                    .success(true)
                    .providerId(providerId)
                    .message(String.format("Email sent to %d recipients via SendGrid", toAddresses.size()))
                    .build();
            
        } catch (Exception e) {
            log.error("Failed to send email via SendGrid: {}", e.getMessage());
            throw new NotificationException("Failed to send email via SendGrid", e);
        }
    }
    
    private void simulateSendGridSend(String subject, String message) {
        // Simulate API call delay
        try {
            Thread.sleep(120 + (long) (Math.random() * 80));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.debug("Simulated SendGrid email: from={} <{}>, to={}, cc={}, bcc={}, subject={}, " +
                "bodyLength={}, templateId={}, categories={}, customArgs={}", 
                fromName, from, toAddresses, ccAddresses, bccAddresses, subject,
                message != null ? message.length() : 0, templateId, categories, customArgs);
    }
    
    /**
     * Builder for SendGridNotifier
     */
    public static class Builder {
        private String apiKey;
        private String from;
        private String fromName = "";
        private List<String> toAddresses = new ArrayList<>();
        private List<String> ccAddresses = new ArrayList<>();
        private List<String> bccAddresses = new ArrayList<>();
        private String replyTo;
        private String templateId;
        private Map<String, String> templateData = new HashMap<>();
        private List<String> categories = new ArrayList<>();
        private Map<String, String> customArgs = new HashMap<>();
        
        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }
        
        public Builder from(String from) {
            this.from = from;
            return this;
        }
        
        public Builder fromName(String fromName) {
            this.fromName = fromName;
            return this;
        }
        
        public Builder addTo(String email) {
            if (email != null && !email.trim().isEmpty()) {
                this.toAddresses.add(email.trim());
            }
            return this;
        }
        
        public Builder addCc(String email) {
            if (email != null && !email.trim().isEmpty()) {
                this.ccAddresses.add(email.trim());
            }
            return this;
        }
        
        public Builder addBcc(String email) {
            if (email != null && !email.trim().isEmpty()) {
                this.bccAddresses.add(email.trim());
            }
            return this;
        }
        
        public Builder replyTo(String replyTo) {
            this.replyTo = replyTo;
            return this;
        }
        
        public Builder templateId(String templateId) {
            this.templateId = templateId;
            return this;
        }
        
        public Builder addTemplateData(String key, String value) {
            this.templateData.put(key, value);
            return this;
        }
        
        public Builder addCategory(String category) {
            if (category != null && !category.trim().isEmpty()) {
                this.categories.add(category.trim());
            }
            return this;
        }
        
        public Builder addCustomArg(String key, String value) {
            this.customArgs.put(key, value);
            return this;
        }
        
        public SendGridNotifier build() {
            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new IllegalArgumentException("SendGrid API key is required");
            }
            if (from == null || from.trim().isEmpty()) {
                throw new IllegalArgumentException("From address is required");
            }
            return new SendGridNotifier(this);
        }
    }
}
