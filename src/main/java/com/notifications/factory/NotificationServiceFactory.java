package com.notifications.factory;

import com.notifications.config.NotificationConfig;
import com.notifications.core.NotificationChannel;
import com.notifications.core.NotificationProvider;
import com.notifications.core.NotificationService;
import com.notifications.provider.EmailNotificationProvider;
import com.notifications.provider.PushNotificationProvider;
import com.notifications.provider.SmsNotificationProvider;
import com.notifications.service.DefaultNotificationService;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for creating NotificationService instances.
 * Factory Pattern: Encapsulates the complex creation logic.
 * 
 * This factory:
 * - Creates and configures providers based on configuration
 * - Registers providers with the service
 * - Handles initialization errors gracefully
 */
@Slf4j
public class NotificationServiceFactory {
    
    /**
     * Creates a NotificationService with the given configuration.
     * 
     * @param config the notification configuration
     * @return configured NotificationService instance
     * @throws IllegalArgumentException if config is null or invalid
     */
    public static NotificationService create(NotificationConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("NotificationConfig cannot be null");
        }
        
        log.info("Creating NotificationService with {} provider configurations", 
                config.getProviders().size());
        
        Map<NotificationChannel, NotificationProvider> providers = new HashMap<>();
        
        // Create and register providers based on configuration
        config.getProviders().forEach((channel, providerConfig) -> {
            try {
                if (!providerConfig.isEnabled()) {
                    log.info("Provider for channel {} is disabled, skipping", channel);
                    return;
                }
                
                NotificationProvider provider = createProvider(channel, providerConfig);
                if (provider != null && provider.isConfigured()) {
                    providers.put(channel, provider);
                    log.info("Registered provider for channel {}: {}", 
                            channel, providerConfig.getProviderName());
                } else {
                    log.warn("Provider for channel {} is not properly configured", channel);
                }
            } catch (Exception e) {
                log.error("Failed to create provider for channel {}: {}", 
                        channel, e.getMessage(), e);
            }
        });
        
        if (providers.isEmpty()) {
            log.warn("No providers were successfully configured!");
        }
        
        DefaultNotificationService service = new DefaultNotificationService(providers);
        log.info("NotificationService created successfully with {} active providers", 
                providers.size());
        
        return service;
    }
    
    /**
     * Creates a provider instance for the given channel.
     * 
     * @param channel the notification channel
     * @param config the provider configuration
     * @return the created provider, or null if channel is not supported
     */
    private static NotificationProvider createProvider(
            NotificationChannel channel, 
            com.notifications.config.ProviderConfig config) {
        
        log.debug("Creating provider for channel {} with provider {}", 
                channel, config.getProviderName());
        
        return switch (channel) {
            case EMAIL -> new EmailNotificationProvider(config);
            case SMS -> new SmsNotificationProvider(config);
            case PUSH -> new PushNotificationProvider(config);
            case SLACK -> {
                log.warn("SLACK channel is not yet implemented");
                yield null;
            }
        };
    }
    
    /**
     * Creates a NotificationService with a custom set of providers.
     * Useful for testing or advanced use cases.
     * 
     * @param providers map of channel to provider
     * @return configured NotificationService instance
     */
    public static NotificationService create(Map<NotificationChannel, NotificationProvider> providers) {
        if (providers == null || providers.isEmpty()) {
            throw new IllegalArgumentException("Providers map cannot be null or empty");
        }
        
        log.info("Creating NotificationService with {} custom providers", providers.size());
        return new DefaultNotificationService(providers);
    }
    
    /**
     * Creates a minimal NotificationService for a single channel.
     * Convenience method for simple use cases.
     * 
     * @param channel the channel to support
     * @param provider the provider for that channel
     * @return configured NotificationService instance
     */
    public static NotificationService createSingleChannel(
            NotificationChannel channel, 
            NotificationProvider provider) {
        
        if (channel == null || provider == null) {
            throw new IllegalArgumentException("Channel and provider cannot be null");
        }
        
        log.info("Creating single-channel NotificationService for {}", channel);
        return new DefaultNotificationService(Map.of(channel, provider));
    }
    
    // Private constructor to prevent instantiation
    private NotificationServiceFactory() {
        throw new UnsupportedOperationException("Factory class cannot be instantiated");
    }
}
