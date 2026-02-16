package com.notifications.service.push;

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
 * Firebase Cloud Messaging (FCM) specific push notifier.
 * Provides FCM-specific features like topics, conditions, and data payloads.
 * 
 * Example usage:
 * <pre>
 * Notifier fcm = FcmNotifier.builder()
 *     .serviceAccountKey("path/to/service-account.json")
 *     .projectId("my-project-id")
 *     .addDeviceToken("device-token-123")
 *     .priority("high")
 *     .addDataField("action", "open_chat")
 *     .build();
 * 
 * // Subject becomes title, message becomes body
 * fcm.send("New Message", "You have a new message from John");
 * </pre>
 */
@Slf4j
public class FcmNotifier implements Notifier {
    
    private final String serviceAccountKey;
    private final String projectId;
    private final List<String> deviceTokens;
    private final String topic;
    private final String condition;
    private final String priority;
    private final Map<String, String> dataFields;
    private final Integer ttl;
    
    private FcmNotifier(Builder builder) {
        this.serviceAccountKey = builder.serviceAccountKey;
        this.projectId = builder.projectId;
        this.deviceTokens = new ArrayList<>(builder.deviceTokens);
        this.topic = builder.topic;
        this.condition = builder.condition;
        this.priority = builder.priority;
        this.dataFields = new HashMap<>(builder.dataFields);
        this.ttl = builder.ttl;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Adds a device token to this notifier.
     * 
     * @param deviceToken the FCM device token
     * @return this notifier for chaining
     */
    public FcmNotifier addDeviceToken(String deviceToken) {
        if (deviceToken != null && !deviceToken.trim().isEmpty()) {
            deviceTokens.add(deviceToken.trim());
        }
        return this;
    }
    
    /**
     * Adds multiple device tokens to this notifier.
     * 
     * @param tokens the device tokens to add
     * @return this notifier for chaining
     */
    public FcmNotifier addDeviceTokens(String... tokens) {
        for (String token : tokens) {
            addDeviceToken(token);
        }
        return this;
    }
    
    @Override
    public NotificationResult send(String subject, String message) throws NotificationException {
        if (deviceTokens.isEmpty() && topic == null && condition == null) {
            throw new NotificationException(
                "No device tokens, topic, or condition configured for FCM notifier");
        }
        
        // Push notifications: subject = title, message = body
        String title = subject != null ? subject : "Notification";
        String body = message != null ? message : "";
        
        String target = getTargetDescription();
        log.info("Sending push notification via FCM to {}: title='{}'", target, title);
        
        try {
            // Simulate FCM API call
            simulateFcmSend(title, body);
            
            String providerId = "fcm-" + UUID.randomUUID();
            
            log.info("Push notification sent successfully via FCM: providerId={}", providerId);
            
            return NotificationResult.builder()
                    .success(true)
                    .providerId(providerId)
                    .message(String.format("Push notification sent to %s via FCM", target))
                    .build();
            
        } catch (Exception e) {
            log.error("Failed to send push notification via FCM: {}", e.getMessage());
            throw new NotificationException("Failed to send push notification via FCM", e);
        }
    }
    
    private String getTargetDescription() {
        if (!deviceTokens.isEmpty()) {
            return deviceTokens.size() + " devices";
        } else if (topic != null) {
            return "topic '" + topic + "'";
        } else if (condition != null) {
            return "condition '" + condition + "'";
        }
        return "unknown target";
    }
    
    private void simulateFcmSend(String title, String body) {
        // Simulate API call delay
        try {
            Thread.sleep(130 + (long) (Math.random() * 100));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.debug("Simulated FCM push: projectId={}, tokens={}, topic={}, condition={}, " +
                "title={}, bodyLength={}, priority={}, dataFields={}, ttl={}", 
                projectId, deviceTokens.size(), topic, condition, title, body.length(), 
                priority, dataFields, ttl);
    }
    
    /**
     * Builder for FcmNotifier
     */
    public static class Builder {
        private String serviceAccountKey;
        private String projectId;
        private List<String> deviceTokens = new ArrayList<>();
        private String topic;
        private String condition;
        private String priority = "high";
        private Map<String, String> dataFields = new HashMap<>();
        private Integer ttl;
        
        public Builder serviceAccountKey(String serviceAccountKey) {
            this.serviceAccountKey = serviceAccountKey;
            return this;
        }
        
        public Builder projectId(String projectId) {
            this.projectId = projectId;
            return this;
        }
        
        public Builder addDeviceToken(String deviceToken) {
            if (deviceToken != null && !deviceToken.trim().isEmpty()) {
                this.deviceTokens.add(deviceToken.trim());
            }
            return this;
        }
        
        public Builder topic(String topic) {
            this.topic = topic;
            return this;
        }
        
        public Builder condition(String condition) {
            this.condition = condition;
            return this;
        }
        
        public Builder priority(String priority) {
            this.priority = priority;
            return this;
        }
        
        public Builder addDataField(String key, String value) {
            this.dataFields.put(key, value);
            return this;
        }
        
        public Builder ttl(Integer ttl) {
            this.ttl = ttl;
            return this;
        }
        
        public FcmNotifier build() {
            if (projectId == null || projectId.trim().isEmpty()) {
                throw new IllegalArgumentException("FCM Project ID is required");
            }
            return new FcmNotifier(this);
        }
    }
}
