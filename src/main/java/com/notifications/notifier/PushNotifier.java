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
 * Push notification notifier implementation.
 * Inspired by Go's FCM service pattern.
 * 
 * This notifier:
 * - Uses subject as notification title
 * - Uses message as notification body
 * - Can have multiple receivers (device tokens)
 * - Simulates sending via configured provider (Firebase, APNs, etc.)
 * 
 * Example usage:
 * <pre>
 * Notifier pushNotifier = PushNotifier.builder()
 *     .providerName("Firebase")
 *     .addReceiver("device-token-123")
 *     .build();
 * 
 * // Subject becomes title, message becomes body
 * pushNotifier.send("New Message", "You have a new message from John");
 * </pre>
 */
@Slf4j
public class PushNotifier implements Notifier {
    
    private final String providerName;
    private final List<String> deviceTokens;
    private final String priority;
    
    private PushNotifier(Builder builder) {
        this.providerName = builder.providerName;
        this.deviceTokens = new ArrayList<>(builder.deviceTokens);
        this.priority = builder.priority;
    }
    
    /**
     * Creates a new builder for PushNotifier.
     * 
     * @return new builder
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Creates a PushNotifier from a ProviderConfig.
     * 
     * @param config the provider configuration
     * @return configured PushNotifier
     */
    public static PushNotifier fromConfig(ProviderConfig config) {
        Builder builder = builder()
            .providerName(config.getProviderName())
            .priority(config.getProperty("priority", "high"));
        
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
     * Adds a receiver (device token) to this notifier.
     * 
     * @param deviceToken the device token to add
     * @return this notifier for chaining
     */
    public PushNotifier addReceiver(String deviceToken) {
        if (deviceToken != null && !deviceToken.trim().isEmpty()) {
            deviceTokens.add(deviceToken.trim());
        }
        return this;
    }
    
    /**
     * Adds multiple receivers (device tokens) to this notifier.
     * 
     * @param deviceTokens the device tokens to add
     * @return this notifier for chaining
     */
    public PushNotifier addReceivers(String... deviceTokens) {
        for (String token : deviceTokens) {
            addReceiver(token);
        }
        return this;
    }
    
    @Override
    public NotificationResult send(String subject, String message) throws NotificationException {
        if (deviceTokens.isEmpty()) {
            throw new NotificationException("No device tokens configured for push notifier");
        }
        
        // Push notifications: subject = title, message = body
        String title = subject != null ? subject : "Notification";
        String body = message != null ? message : "";
        
        log.info("Sending push notification via {} to {} devices: title='{}'", 
                providerName, deviceTokens.size(), title);
        
        try {
            // Simulate sending push notification
            simulatePushSend(title, body);
            
            String providerId = providerName.toLowerCase() + "-" + UUID.randomUUID();
            
            log.info("Push notification sent successfully via {}: providerId={}", providerName, providerId);
            
            return NotificationResult.builder()
                    .success(true)
                    .channel(NotificationChannel.PUSH)
                    .providerId(providerId)
                    .message(String.format("Push notification sent to %d devices via %s", 
                            deviceTokens.size(), providerName))
                    .build();
            
        } catch (Exception e) {
            log.error("Failed to send push notification via {}: {}", providerName, e.getMessage());
            throw new NotificationException("Failed to send push notification", e);
        }
    }
    
    private void simulatePushSend(String title, String body) {
        // Simulate API call delay
        try {
            Thread.sleep(120 + (long) (Math.random() * 100));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.debug("Simulated push: provider={}, tokens={}, title={}, bodyLength={}, priority={}", 
                providerName, deviceTokens.size(), title, body.length(), priority);
    }
    
    /**
     * Builder for PushNotifier
     */
    public static class Builder {
        private String providerName = "Firebase";
        private List<String> deviceTokens = new ArrayList<>();
        private String priority = "high";
        
        public Builder providerName(String providerName) {
            this.providerName = providerName;
            return this;
        }
        
        public Builder addReceiver(String deviceToken) {
            if (deviceToken != null && !deviceToken.trim().isEmpty()) {
                this.deviceTokens.add(deviceToken.trim());
            }
            return this;
        }
        
        public Builder addReceivers(String... deviceTokens) {
            for (String token : deviceTokens) {
                addReceiver(token);
            }
            return this;
        }
        
        public Builder priority(String priority) {
            this.priority = priority;
            return this;
        }
        
        public PushNotifier build() {
            return new PushNotifier(this);
        }
    }
}
