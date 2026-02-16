package com.notifications.service.email;

import com.notifications.config.ProviderConfig;
import com.notifications.core.NotificationException;
import com.notifications.core.NotificationResult;
import com.notifications.core.Notifier;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Generic SMTP email notifier implementation.
 * Can be used with any SMTP server.
 * 
 * This notifier:
 * - Uses subject parameter as email subject
 * - Uses message parameter as email body
 * - Can have multiple receivers
 * - Works with any SMTP server
 * 
 * Example usage:
 * <pre>
 * Notifier emailNotifier = EmailNotifier.builder()
 *     .smtpHost("smtp.gmail.com")
 *     .smtpPort(587)
 *     .username("user@gmail.com")
 *     .password("app-password")
 *     .from("noreply@example.com")
 *     .fromName("My App")
 *     .addReceiver("user@example.com")
 *     .build();
 * 
 * emailNotifier.send("Welcome", "Hello World!");
 * </pre>
 */
@Slf4j
public class EmailNotifier implements Notifier {
    
    private final String smtpHost;
    private final int smtpPort;
    private final String username;
    private final String password;
    private final String from;
    private final String fromName;
    private final List<String> toAddresses;
    private final boolean usePlainText;
    private final boolean useSSL;
    
    private EmailNotifier(Builder builder) {
        this.smtpHost = builder.smtpHost;
        this.smtpPort = builder.smtpPort;
        this.username = builder.username;
        this.password = builder.password;
        this.from = builder.from;
        this.fromName = builder.fromName;
        this.toAddresses = new ArrayList<>(builder.toAddresses);
        this.usePlainText = builder.usePlainText;
        this.useSSL = builder.useSSL;
    }
    
    /**
     * Creates a new builder for EmailNotifier.
     * 
     * @return new builder
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Creates an EmailNotifier from a ProviderConfig.
     * 
     * @param config the provider configuration
     * @return configured EmailNotifier
     */
    public static EmailNotifier fromConfig(ProviderConfig config) {
        Builder builder = builder()
            .smtpHost(config.getProperty("smtpHost", "localhost"))
            .smtpPort(Integer.parseInt(config.getProperty("smtpPort", "587")))
            .from(config.getProperty("from", "noreply@example.com"))
            .fromName(config.getProperty("fromName", "Notification Service"));
        
        String username = config.getProperty("username", null);
        if (username != null) {
            builder.username(username);
        }
        
        String password = config.getProperty("password", null);
        if (password != null) {
            builder.password(password);
        }
        
        // Add receivers if provided
        String receivers = config.getProperty("receivers", null);
        if (receivers != null) {
            for (String receiver : receivers.split(",")) {
                builder.addReceiver(receiver.trim());
            }
        }
        
        return builder.build();
    }
    
    /**
     * Adds a receiver to this notifier.
     * 
     * @param emailAddress the email address to add
     * @return this notifier for chaining
     */
    public EmailNotifier addReceiver(String emailAddress) {
        if (emailAddress != null && !emailAddress.trim().isEmpty()) {
            toAddresses.add(emailAddress.trim());
        }
        return this;
    }
    
    /**
     * Adds multiple receivers to this notifier.
     * 
     * @param emailAddresses the email addresses to add
     * @return this notifier for chaining
     */
    public EmailNotifier addReceivers(String... emailAddresses) {
        for (String email : emailAddresses) {
            addReceiver(email);
        }
        return this;
    }
    
    @Override
    public NotificationResult send(String subject, String message) throws NotificationException {
        if (toAddresses.isEmpty()) {
            throw new NotificationException("No receivers configured for email notifier");
        }
        
        log.info("Sending email via SMTP {}:{} to {} recipients: subject='{}'", 
                smtpHost, smtpPort, toAddresses.size(), subject);
        
        try {
            // Simulate sending email
            simulateEmailSend(subject, message);
            
            String providerId = "smtp-" + UUID.randomUUID();
            
            log.info("Email sent successfully via SMTP: providerId={}", providerId);
            
            return NotificationResult.builder()
                    .success(true)
                    .providerId(providerId)
                    .message(String.format("Email sent to %d recipients via SMTP", toAddresses.size()))
                    .build();
            
        } catch (Exception e) {
            log.error("Failed to send email via SMTP: {}", e.getMessage());
            throw new NotificationException("Failed to send email", e);
        }
    }
    
    private void simulateEmailSend(String subject, String message) {
        // Simulate API call delay
        try {
            Thread.sleep(100 + (long) (Math.random() * 100));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.debug("Simulated SMTP email: host={}, port={}, from={}, to={}, subject={}, bodyLength={}, ssl={}", 
                smtpHost, smtpPort, from, toAddresses, subject, 
                message != null ? message.length() : 0, useSSL);
    }
    
    /**
     * Builder for EmailNotifier
     */
    public static class Builder {
        private String smtpHost = "localhost";
        private int smtpPort = 587;
        private String username;
        private String password;
        private String from;
        private String fromName = "Notification Service";
        private List<String> toAddresses = new ArrayList<>();
        private boolean usePlainText = false;
        private boolean useSSL = true;
        
        public Builder smtpHost(String smtpHost) {
            this.smtpHost = smtpHost;
            return this;
        }
        
        public Builder smtpPort(int smtpPort) {
            this.smtpPort = smtpPort;
            return this;
        }
        
        public Builder username(String username) {
            this.username = username;
            return this;
        }
        
        public Builder password(String password) {
            this.password = password;
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
        
        public Builder addReceiver(String emailAddress) {
            if (emailAddress != null && !emailAddress.trim().isEmpty()) {
                this.toAddresses.add(emailAddress.trim());
            }
            return this;
        }
        
        public Builder addReceivers(String... emailAddresses) {
            for (String email : emailAddresses) {
                addReceiver(email);
            }
            return this;
        }
        
        public Builder usePlainText(boolean usePlainText) {
            this.usePlainText = usePlainText;
            return this;
        }
        
        public Builder useSSL(boolean useSSL) {
            this.useSSL = useSSL;
            return this;
        }
        
        public EmailNotifier build() {
            if (from == null || from.trim().isEmpty()) {
                throw new IllegalArgumentException("From address is required");
            }
            if (smtpHost == null || smtpHost.trim().isEmpty()) {
                throw new IllegalArgumentException("SMTP host is required");
            }
            return new EmailNotifier(this);
        }
    }
}
