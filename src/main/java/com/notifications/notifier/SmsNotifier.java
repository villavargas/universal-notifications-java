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
 * SMS notifier implementation.
 * Inspired by Go's Twilio service pattern.
 * 
 * This notifier:
 * - Concatenates subject + message (SMS has no subject concept)
 * - Can have multiple receivers (phone numbers)
 * - Simulates sending via configured provider (Twilio, Plivo, etc.)
 * 
 * Example usage:
 * <pre>
 * Notifier smsNotifier = SmsNotifier.builder()
 *     .providerName("Twilio")
 *     .fromPhoneNumber("+15551234567")
 *     .addReceiver("+15559876543")
 *     .build();
 * 
 * // Subject and message are concatenated for SMS
 * smsNotifier.send("Alert", "System is down!");
 * // Sends: "Alert\nSystem is down!"
 * </pre>
 */
@Slf4j
public class SmsNotifier implements Notifier {
    
    private final String providerName;
    private final String fromPhoneNumber;
    private final List<String> toPhoneNumbers;
    
    private SmsNotifier(Builder builder) {
        this.providerName = builder.providerName;
        this.fromPhoneNumber = builder.fromPhoneNumber;
        this.toPhoneNumbers = new ArrayList<>(builder.toPhoneNumbers);
    }
    
    /**
     * Creates a new builder for SmsNotifier.
     * 
     * @return new builder
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Creates an SmsNotifier from a ProviderConfig.
     * 
     * @param config the provider configuration
     * @return configured SmsNotifier
     */
    public static SmsNotifier fromConfig(ProviderConfig config) {
        Builder builder = builder()
            .providerName(config.getProviderName())
            .fromPhoneNumber(config.getProperty("fromPhoneNumber", "+10000000000"));
        
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
     * @param phoneNumber the phone number to add
     * @return this notifier for chaining
     */
    public SmsNotifier addReceiver(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            toPhoneNumbers.add(phoneNumber.trim());
        }
        return this;
    }
    
    /**
     * Adds multiple receivers to this notifier.
     * 
     * @param phoneNumbers the phone numbers to add
     * @return this notifier for chaining
     */
    public SmsNotifier addReceivers(String... phoneNumbers) {
        for (String phone : phoneNumbers) {
            addReceiver(phone);
        }
        return this;
    }
    
    @Override
    public NotificationResult send(String subject, String message) throws NotificationException {
        if (toPhoneNumbers.isEmpty()) {
            throw new NotificationException("No receivers configured for SMS notifier");
        }
        
        // SMS doesn't have subject concept, concatenate both
        String smsBody = buildSmsBody(subject, message);
        
        log.info("Sending SMS via {} to {} recipients: bodyLength={}", 
                providerName, toPhoneNumbers.size(), smsBody.length());
        
        try {
            // Simulate sending SMS
            simulateSmsSend(smsBody);
            
            String providerId = providerName.toLowerCase() + "-" + UUID.randomUUID();
            
            log.info("SMS sent successfully via {}: providerId={}", providerName, providerId);
            
            return NotificationResult.builder()
                    .success(true)
                    .channel(NotificationChannel.SMS)
                    .providerId(providerId)
                    .message(String.format("SMS sent to %d recipients via %s", 
                            toPhoneNumbers.size(), providerName))
                    .build();
            
        } catch (Exception e) {
            log.error("Failed to send SMS via {}: {}", providerName, e.getMessage());
            throw new NotificationException("Failed to send SMS", e);
        }
    }
    
    /**
     * Builds the SMS body by concatenating subject and message.
     * SMS doesn't have a subject concept, so we combine them.
     */
    private String buildSmsBody(String subject, String message) {
        if (subject == null || subject.trim().isEmpty()) {
            return message != null ? message : "";
        }
        if (message == null || message.trim().isEmpty()) {
            return subject;
        }
        return subject + "\n" + message;
    }
    
    private void simulateSmsSend(String body) {
        // Simulate API call delay
        try {
            Thread.sleep(150 + (long) (Math.random() * 100));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.debug("Simulated SMS: from={}, to={}, bodyLength={}", 
                fromPhoneNumber, toPhoneNumbers, body.length());
    }
    
    /**
     * Builder for SmsNotifier
     */
    public static class Builder {
        private String providerName = "Twilio";
        private String fromPhoneNumber;
        private List<String> toPhoneNumbers = new ArrayList<>();
        
        public Builder providerName(String providerName) {
            this.providerName = providerName;
            return this;
        }
        
        public Builder fromPhoneNumber(String fromPhoneNumber) {
            this.fromPhoneNumber = fromPhoneNumber;
            return this;
        }
        
        public Builder addReceiver(String phoneNumber) {
            if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                this.toPhoneNumbers.add(phoneNumber.trim());
            }
            return this;
        }
        
        public Builder addReceivers(String... phoneNumbers) {
            for (String phone : phoneNumbers) {
                addReceiver(phone);
            }
            return this;
        }
        
        public SmsNotifier build() {
            if (fromPhoneNumber == null || fromPhoneNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("From phone number is required");
            }
            return new SmsNotifier(this);
        }
    }
}
