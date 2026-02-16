package com.notifications.notifier;

import com.notifications.config.ProviderConfig;
import com.notifications.core.NotificationChannel;
import com.notifications.core.NotificationException;
import com.notifications.core.NotificationResult;
import com.notifications.core.Notifier;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Email notifier implementation.
 * Inspired by Go's mail service pattern.
 * 
 * @deprecated This class has been moved to {@link com.notifications.service.email.EmailNotifier}.
 *             This version is kept for backward compatibility.
 *             For new code, use the service-based notifiers from the service package.
 * @see com.notifications.service.email.EmailNotifier
 * @see com.notifications.service.email.SendGridNotifier
 */
@Slf4j
@Deprecated(since = "2.0.0", forRemoval = false)
public class EmailNotifier implements Notifier {
    
    private final String providerName;
    private final String senderAddress;
    private final String senderName;
    private final List<String> receiverAddresses;
    private final boolean usePlainText;
    
    private EmailNotifier(Builder builder) {
        this.providerName = builder.providerName;
        this.senderAddress = builder.senderAddress;
        this.senderName = builder.senderName;
        this.receiverAddresses = new ArrayList<>(builder.receiverAddresses);
        this.usePlainText = builder.usePlainText;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static EmailNotifier fromConfig(ProviderConfig config) {
        Builder builder = builder()
            .providerName(config.getProviderName())
            .senderAddress(config.getProperty("senderEmail", "noreply@example.com"))
            .senderName(config.getProperty("senderName", "Notification Service"));
        
        String receivers = config.getProperty("receivers", null);
        if (receivers != null) {
            for (String receiver : receivers.split(",")) {
                builder.addReceiver(receiver.trim());
            }
        }
        
        return builder.build();
    }
    
    public EmailNotifier addReceiver(String emailAddress) {
        if (emailAddress != null && !emailAddress.trim().isEmpty()) {
            receiverAddresses.add(emailAddress.trim());
        }
        return this;
    }
    
    public EmailNotifier addReceivers(String... emailAddresses) {
        for (String email : emailAddresses) {
            addReceiver(email);
        }
        return this;
    }
    
    @Override
    public NotificationResult send(String subject, String message) throws NotificationException {
        if (receiverAddresses.isEmpty()) {
            throw new NotificationException("No receivers configured for email notifier");
        }
        
        log.info("Sending email via {} to {} recipients: subject='{}'", 
                providerName, receiverAddresses.size(), subject);
        
        try {
            simulateEmailSend(subject, message);
            
            String providerId = providerName.toLowerCase() + "-" + UUID.randomUUID();
            
            log.info("Email sent successfully via {}: providerId={}", providerName, providerId);
            
            return NotificationResult.builder()
                    .success(true)
                    .channel(NotificationChannel.EMAIL)
                    .providerId(providerId)
                    .message(String.format("Email sent to %d recipients via %s", 
                            receiverAddresses.size(), providerName))
                    .build();
            
        } catch (Exception e) {
            log.error("Failed to send email via {}: {}", providerName, e.getMessage());
            throw new NotificationException("Failed to send email", e);
        }
    }
    
    private void simulateEmailSend(String subject, String message) {
        try {
            Thread.sleep(100 + (long) (Math.random() * 100));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.debug("Simulated email: from={}, to={}, subject={}, bodyLength={}, format={}", 
                senderAddress, receiverAddresses, subject, 
                message != null ? message.length() : 0,
                usePlainText ? "plain" : "html");
    }
    
    public static class Builder {
        private String providerName = "SMTP";
        private String senderAddress;
        private String senderName = "Notification Service";
        private List<String> receiverAddresses = new ArrayList<>();
        private boolean usePlainText = false;
        
        public Builder providerName(String providerName) {
            this.providerName = providerName;
            return this;
        }
        
        public Builder senderAddress(String senderAddress) {
            this.senderAddress = senderAddress;
            return this;
        }
        
        public Builder senderName(String senderName) {
            this.senderName = senderName;
            return this;
        }
        
        public Builder addReceiver(String emailAddress) {
            if (emailAddress != null && !emailAddress.trim().isEmpty()) {
                this.receiverAddresses.add(emailAddress.trim());
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
        
        public EmailNotifier build() {
            if (senderAddress == null || senderAddress.trim().isEmpty()) {
                throw new IllegalArgumentException("Sender address is required");
            }
            return new EmailNotifier(this);
        }
    }
}
