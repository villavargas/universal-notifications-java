package com.notifications.service;

import com.notifications.core.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Default implementation of NotificationService.
 * 
 * This service:
 * - Manages multiple notification providers
 * - Routes notifications to the appropriate provider based on channel
 * - Supports both synchronous and asynchronous sending
 * - Provides batch sending capabilities
 * - Handles errors gracefully
 */
@Slf4j
public class DefaultNotificationService implements NotificationService {
    
    private final Map<NotificationChannel, NotificationProvider> providers;
    
    /**
     * Creates a new DefaultNotificationService with the given providers.
     * 
     * @param providers map of channel to provider
     */
    public DefaultNotificationService(Map<NotificationChannel, NotificationProvider> providers) {
        if (providers == null || providers.isEmpty()) {
            throw new IllegalArgumentException("At least one provider must be configured");
        }
        this.providers = new HashMap<>(providers);
        log.info("DefaultNotificationService initialized with {} providers", providers.size());
    }
    
    @Override
    public NotificationResult send(Notification notification) throws NotificationException {
        if (notification == null) {
            throw new IllegalArgumentException("Notification cannot be null");
        }
        
        NotificationChannel channel = notification.getChannel();
        log.debug("Sending notification via channel: {}", channel);
        
        NotificationProvider provider = providers.get(channel);
        if (provider == null) {
            String error = String.format("No provider configured for channel: %s", channel);
            log.error(error);
            throw new ProviderException(error);
        }
        
        if (!provider.isConfigured()) {
            String error = String.format("Provider for channel %s is not properly configured", channel);
            log.error(error);
            throw new ProviderException(error);
        }
        
        try {
            NotificationResult result = provider.send(notification);
            log.debug("Notification sent successfully via {}: {}", 
                    channel, result.getProviderId());
            return result;
        } catch (NotificationException e) {
            log.error("Failed to send notification via {}: {}", channel, e.getMessage());
            throw e;
        }
    }
    
    @Override
    public CompletableFuture<NotificationResult> sendAsync(Notification notification) {
        if (notification == null) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException("Notification cannot be null"));
        }
        
        NotificationChannel channel = notification.getChannel();
        NotificationProvider provider = providers.get(channel);
        
        if (provider == null) {
            String error = String.format("No provider configured for channel: %s", channel);
            log.error(error);
            return CompletableFuture.completedFuture(
                    NotificationResult.failure(notification.getId(), channel, error));
        }
        
        if (!provider.isConfigured()) {
            String error = String.format("Provider for channel %s is not properly configured", channel);
            log.error(error);
            return CompletableFuture.completedFuture(
                    NotificationResult.failure(notification.getId(), channel, error));
        }
        
        log.debug("Sending notification asynchronously via channel: {}", channel);
        return provider.sendAsync(notification)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        log.error("Async notification failed via {}: {}", 
                                channel, throwable.getMessage());
                    } else {
                        log.debug("Async notification completed via {}: success={}", 
                                channel, result.isSuccess());
                    }
                });
    }
    
    @Override
    public CompletableFuture<List<NotificationResult>> sendBatch(List<Notification> notifications) {
        if (notifications == null || notifications.isEmpty()) {
            log.warn("sendBatchAsync called with empty notification list");
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
        
        log.info("Sending batch of {} notifications asynchronously", notifications.size());
        
        List<CompletableFuture<NotificationResult>> futures = notifications.stream()
                .map(this::sendAsync)
                .collect(Collectors.toList());
        
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()))
                .whenComplete((results, throwable) -> {
                    if (throwable != null) {
                        log.error("Batch async operation failed: {}", throwable.getMessage());
                    } else {
                        long successCount = results.stream().filter(NotificationResult::isSuccess).count();
                        log.info("Batch async completed: {}/{} successful", successCount, results.size());
                    }
                });
    }
    
    @Override
    public NotificationProvider getProvider(NotificationChannel channel) {
        return providers.get(channel);
    }
    
    @Override
    public boolean isChannelSupported(NotificationChannel channel) {
        boolean supported = providers.containsKey(channel);
        log.trace("Channel {} supported: {}", channel, supported);
        return supported;
    }
    
    /**
     * Gets all supported channels.
     * 
     * @return set of supported channels
     */
    public Set<NotificationChannel> getSupportedChannels() {
        Set<NotificationChannel> channels = providers.keySet();
        log.trace("Supported channels: {}", channels);
        return new HashSet<>(channels);
    }
    
    /**
     * Checks if all providers are healthy.
     * 
     * @return true if all providers are configured correctly
     */
    public boolean isHealthy() {
        boolean allHealthy = providers.values().stream()
                .allMatch(NotificationProvider::isConfigured);
        
        log.debug("Service health check: {}", allHealthy ? "healthy" : "unhealthy");
        return allHealthy;
    }
    
    /**
     * Gets statistics about the service configuration.
     * 
     * @return map with service statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProviders", providers.size());
        stats.put("supportedChannels", getSupportedChannels());
        stats.put("healthy", isHealthy());
        
        Map<NotificationChannel, Boolean> providerHealth = new HashMap<>();
        providers.forEach((channel, provider) -> 
                providerHealth.put(channel, provider.isConfigured()));
        stats.put("providerHealth", providerHealth);
        
        return stats;
    }
}
