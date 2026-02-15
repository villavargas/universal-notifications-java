package com.notifications.provider;

import com.notifications.config.ProviderConfig;
import com.notifications.core.*;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Abstract base class for notification providers.
 * Template Method Pattern: Defines the skeleton of the algorithm.
 */
@Slf4j
public abstract class AbstractNotificationProvider implements NotificationProvider {
    
    protected final ProviderConfig config;
    
    protected AbstractNotificationProvider(ProviderConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Provider configuration cannot be null");
        }
        this.config = config;
        config.validate();
    }
    
    @Override
    public NotificationResult send(Notification notification) throws NotificationException {
        log.info("Sending via {} - Channel: {}, ID: {}", getProviderName(), getChannel(), notification.getId());
        
        try {
            if (config.getProperties().getOrDefault("validateBeforeSend", true).equals(true)) {
                validate(notification);
            }
            
            NotificationResult result = doSend(notification);
            
            if (result.isSuccess()) {
                log.info("Sent successfully via {} - ID: {}", getProviderName(), notification.getId());
            }
            
            return result;
        } catch (ValidationException e) {
            log.error("Validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error sending: {}", e.getMessage());
            throw new ProviderException("Failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public CompletableFuture<NotificationResult> sendAsync(Notification notification) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return send(notification);
            } catch (NotificationException e) {
                return NotificationResult.failure(notification.getId(), getChannel(), e.getMessage());
            }
        });
    }
    
    @Override
    public boolean isConfigured() {
        return config != null && config.isEnabled() && config.getApiKey() != null;
    }
    
    @Override
    public void validate(Notification notification) throws ValidationException {
        if (notification == null) throw new ValidationException("Notification cannot be null");
        if (notification.getChannel() != getChannel()) {
            throw new ValidationException("Channel mismatch");
        }
        if (notification.getRecipient() == null) throw new ValidationException("Recipient required");
        if (notification.getBody() == null) throw new ValidationException("Body required");
        
        validateProviderSpecific(notification);
    }
    
    protected abstract NotificationResult doSend(Notification notification) throws NotificationException;
    
    protected void validateProviderSpecific(Notification notification) throws ValidationException {
    }
    
    protected String generateProviderId() {
        return getProviderName().toLowerCase() + "-" + UUID.randomUUID();
    }
    
    protected void simulateNetworkLatency() {
        try {
            Thread.sleep(100 + (long)(Math.random() * 200));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
