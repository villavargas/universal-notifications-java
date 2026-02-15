package com.notifications.provider;

import com.notifications.config.ProviderConfig;
import com.notifications.core.*;
import com.notifications.util.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PushNotificationProvider extends AbstractNotificationProvider {
    
    private static final int MAX_TITLE_LENGTH = 65;
    private static final int MAX_BODY_LENGTH = 240;
    
    public PushNotificationProvider(ProviderConfig config) {
        super(config);
        log.info("Initialized {} push provider", getProviderName());
    }
    
    @Override
    protected NotificationResult doSend(Notification notification) throws NotificationException {
        log.debug("Sending push - To device: {}", notification.getRecipient());
        
        simulateNetworkLatency();
        String providerId = generateProviderId();
        
        Map<String, Object> request = buildPushRequest(notification);
        log.info("Simulated push request: {}", request);
        
        // Uncomment to simulate random errors (2% chance)
        // if (Math.random() < 0.02) {
        //     throw new ProviderException("Simulated error: Invalid device token");
        // }
        
        return NotificationResult.builder()
                .notificationId(notification.getId())
                .success(true)
                .channel(NotificationChannel.PUSH)
                .providerId(providerId)
                .message("Push sent via " + getProviderName())
                .metadata(request)
                .build();
    }
    
    @Override
    protected void validateProviderSpecific(Notification notification) throws ValidationException {
        if (!ValidationUtils.isValidDeviceToken(notification.getRecipient())) {
            throw new ValidationException("Invalid device token");
        }
        String title = notification.getSubject();
        if (title != null && title.length() > MAX_TITLE_LENGTH) {
            throw new ValidationException("Title too long");
        }
        if (notification.getBody().length() > MAX_BODY_LENGTH) {
            throw new ValidationException("Body too long");
        }
    }
    
    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.PUSH;
    }
    
    @Override
    public String getProviderName() {
        return config.getProviderName();
    }
    
    private Map<String, Object> buildPushRequest(Notification notification) {
        Map<String, Object> request = new HashMap<>();
        request.put("token", notification.getRecipient());
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("title", notification.getSubject() != null ? notification.getSubject() : "Notification");
        payload.put("body", notification.getBody());
        payload.put("sound", "default");
        
        request.put("notification", payload);
        request.put("priority", "high");
        
        return request;
    }
}
