package com.notifications.config;

import com.notifications.core.NotificationChannel;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.HashMap;
import java.util.Map;

/**
 * Main configuration for the notification library.
 * Contains all provider configurations.
 * 
 * Uses Builder pattern for flexible construction.
 */
@Getter
@Builder
public class NotificationConfig {
    
    /**
     * Map of channel to provider configuration
     * Allows multiple providers per channel (for fallback)
     */
    @Singular("provider")
    private final Map<NotificationChannel, ProviderConfig> providers;
    
    /**
     * Enable async processing by default
     */
    @Builder.Default
    private final boolean asyncByDefault = false;
    
    /**
     * Enable automatic retries on failure
     */
    @Builder.Default
    private final boolean autoRetry = true;
    
    /**
     * Global timeout in seconds
     */
    @Builder.Default
    private final int globalTimeoutSeconds = 60;
    
    /**
     * Enable validation before sending
     */
    @Builder.Default
    private final boolean validateBeforeSend = true;
    
    /**
     * Gets the provider configuration for a specific channel
     */
    public ProviderConfig getProviderConfig(NotificationChannel channel) {
        return providers.get(channel);
    }
    
    /**
     * Checks if a channel is configured
     */
    public boolean isChannelConfigured(NotificationChannel channel) {
        ProviderConfig config = providers.get(channel);
        return config != null && config.isEnabled();
    }
    
    /**
     * Validates the entire configuration
     */
    public void validate() {
        if (providers == null || providers.isEmpty()) {
            throw new IllegalArgumentException("At least one provider must be configured");
        }
        
        // Validate each provider configuration
        providers.values().forEach(ProviderConfig::validate);
    }
}
