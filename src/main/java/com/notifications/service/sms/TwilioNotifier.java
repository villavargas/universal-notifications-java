package com.notifications.service.sms;

import com.notifications.core.NotificationException;
import com.notifications.core.NotificationResult;
import com.notifications.core.Notifier;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Twilio-specific SMS notifier.
 * Provides Twilio-specific features.
 * 
 * Example usage:
 * <pre>
 * Notifier twilio = TwilioNotifier.builder()
 *     .accountSid("AC...")
 *     .authToken("...")
 *     .fromPhoneNumber("+15551234567")
 *     .addTo("+15559876543")
 *     .messagingServiceSid("MG...")  // Optional
 *     .build();
 * 
 * // Subject and message are concatenated for SMS
 * twilio.send("Alert", "System is down!");
 * </pre>
 */
@Slf4j
public class TwilioNotifier implements Notifier {
    
    private final String accountSid;
    private final String authToken;
    private final String fromPhoneNumber;
    private final List<String> toPhoneNumbers;
    private final String messagingServiceSid;
    private final Integer maxPrice;
    
    private TwilioNotifier(Builder builder) {
        this.accountSid = builder.accountSid;
        this.authToken = builder.authToken;
        this.fromPhoneNumber = builder.fromPhoneNumber;
        this.toPhoneNumbers = new ArrayList<>(builder.toPhoneNumbers);
        this.messagingServiceSid = builder.messagingServiceSid;
        this.maxPrice = builder.maxPrice;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Adds a recipient phone number to this notifier.
     * 
     * @param phoneNumber the phone number to add (E.164 format recommended)
     * @return this notifier for chaining
     */
    public TwilioNotifier addTo(String phoneNumber) {
        if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
            toPhoneNumbers.add(phoneNumber.trim());
        }
        return this;
    }
    
    /**
     * Adds multiple recipient phone numbers to this notifier.
     * 
     * @param phoneNumbers the phone numbers to add
     * @return this notifier for chaining
     */
    public TwilioNotifier addTo(String... phoneNumbers) {
        for (String phone : phoneNumbers) {
            addTo(phone);
        }
        return this;
    }
    
    @Override
    public NotificationResult send(String subject, String message) throws NotificationException {
        if (toPhoneNumbers.isEmpty()) {
            throw new NotificationException("No recipients configured for Twilio notifier");
        }
        
        // SMS doesn't have subject concept, concatenate both
        String smsBody = buildSmsBody(subject, message);
        
        log.info("Sending SMS via Twilio to {} recipients: bodyLength={}", 
                toPhoneNumbers.size(), smsBody.length());
        
        try {
            // Simulate Twilio API call
            simulateTwilioSend(smsBody);
            
            String providerId = "twilio-" + UUID.randomUUID();
            
            log.info("SMS sent successfully via Twilio: providerId={}", providerId);
            
            return NotificationResult.builder()
                    .success(true)
                    .providerId(providerId)
                    .message(String.format("SMS sent to %d recipients via Twilio", toPhoneNumbers.size()))
                    .build();
            
        } catch (Exception e) {
            log.error("Failed to send SMS via Twilio: {}", e.getMessage());
            throw new NotificationException("Failed to send SMS via Twilio", e);
        }
    }
    
    private String buildSmsBody(String subject, String message) {
        if (subject == null || subject.trim().isEmpty()) {
            return message != null ? message : "";
        }
        if (message == null || message.trim().isEmpty()) {
            return subject;
        }
        return subject + "\n" + message;
    }
    
    private void simulateTwilioSend(String body) {
        // Simulate API call delay
        try {
            Thread.sleep(150 + (long) (Math.random() * 100));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.debug("Simulated Twilio SMS: accountSid={}, from={}, to={}, bodyLength={}, messagingServiceSid={}", 
                accountSid, fromPhoneNumber, toPhoneNumbers, body.length(), messagingServiceSid);
    }
    
    /**
     * Builder for TwilioNotifier
     */
    public static class Builder {
        private String accountSid;
        private String authToken;
        private String fromPhoneNumber;
        private List<String> toPhoneNumbers = new ArrayList<>();
        private String messagingServiceSid;
        private Integer maxPrice;
        
        public Builder accountSid(String accountSid) {
            this.accountSid = accountSid;
            return this;
        }
        
        public Builder authToken(String authToken) {
            this.authToken = authToken;
            return this;
        }
        
        public Builder fromPhoneNumber(String fromPhoneNumber) {
            this.fromPhoneNumber = fromPhoneNumber;
            return this;
        }
        
        public Builder addTo(String phoneNumber) {
            if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                this.toPhoneNumbers.add(phoneNumber.trim());
            }
            return this;
        }
        
        public Builder messagingServiceSid(String messagingServiceSid) {
            this.messagingServiceSid = messagingServiceSid;
            return this;
        }
        
        public Builder maxPrice(Integer maxPrice) {
            this.maxPrice = maxPrice;
            return this;
        }
        
        public TwilioNotifier build() {
            if (accountSid == null || accountSid.trim().isEmpty()) {
                throw new IllegalArgumentException("Twilio Account SID is required");
            }
            if (authToken == null || authToken.trim().isEmpty()) {
                throw new IllegalArgumentException("Twilio Auth Token is required");
            }
            if (fromPhoneNumber == null || fromPhoneNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("From phone number is required");
            }
            return new TwilioNotifier(this);
        }
    }
}
