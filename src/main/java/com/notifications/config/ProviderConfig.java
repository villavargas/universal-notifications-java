package com.notifications.config;

import com.notifications.core.NotificationChannel;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.Map;

/**
 * Configuration for a specific notification provider.
 * Uses Builder pattern for flexible configuration.
 */
@Getter
@Builder
public class ProviderConfig {
    
    /**
     * The channel this provider handles
     */
    private final NotificationChannel channel;
    
    /**
     * Provider name (e.g., "SendGrid", "Twilio", "Firebase")
     */
    private final String providerName;
    
    /**
     * API key or authentication token
     */
    private final String apiKey;
    
    /**
     * Optional API secret
     */
    private final String apiSecret;
    
    /**
     * API endpoint URL (if custom)
     */
    private final String endpoint;
    
    /**
     * Sender/From identifier
     * - Email: from email address
     * - SMS: sender phone number or sender ID
     * - Push: app identifier
     */
    private final String from;
    
    /**
     * Maximum retry attempts on failure
     */
    @Builder.Default
    private final int maxRetries = 3;
    
    /**
     * Timeout in seconds
     */
    @Builder.Default
    private final int timeoutSeconds = 30;
    
    /**
     * Enable/disable this provider
     */
    @Builder.Default
    private final boolean enabled = true;
    
    /**
     * Additional provider-specific properties
     * Examples:
     * - SendGrid: "templateId", "categories"
     * - Twilio: "messagingServiceSid"
     * - Firebase: "projectId", "serviceAccountKey"
     */
    @Singular
    private final Map<String, Object> properties;
    
    /**
     * Validates that required fields are present
     */
    public void validate() {
        if (channel == null) {
            throw new IllegalArgumentException("Channel is required");
        }
        if (providerName == null || providerName.isBlank()) {
            throw new IllegalArgumentException("Provider name is required");
        }
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("API key is required for " + providerName);
        }
    }
    
    /**
     * Gets a property with a default value
     */
    public String getProperty(String key, String defaultValue) {
        if (properties == null) {
            return defaultValue;
        }
        Object value = properties.get(key);
        return value != null ? value.toString() : defaultValue;
    }
    
    /**
     * Gets a property (may return null)
     */
    public String getProperty(String key) {
        return getProperty(key, null);
    }
}
