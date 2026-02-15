package com.notifications.provider;

import com.notifications.config.ProviderConfig;
import com.notifications.core.*;
import com.notifications.util.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class EmailNotificationProvider extends AbstractNotificationProvider {
    
    public EmailNotificationProvider(ProviderConfig config) {
        super(config);
        log.info("Initialized {} email provider", getProviderName());
    }
    
    @Override
    protected NotificationResult doSend(Notification notification) throws NotificationException {
        log.debug("Sending email - To: {}, Subject: {}", notification.getRecipient(), notification.getSubject());
        
        simulateNetworkLatency();
        String providerId = generateProviderId();
        
        Map<String, Object> request = buildEmailRequest(notification);
        log.info("Simulated email request: {}", request);
        
        if (Math.random() < 0.05) {
            throw new ProviderException("Simulated error: Rate limit exceeded");
        }
        
        return NotificationResult.builder()
                .notificationId(notification.getId())
                .success(true)
                .channel(NotificationChannel.EMAIL)
                .providerId(providerId)
                .message("Email sent via " + getProviderName())
                .metadata(request)
                .build();
    }
    
    @Override
    protected void validateProviderSpecific(Notification notification) throws ValidationException {
        if (!ValidationUtils.isValidEmail(notification.getRecipient())) {
            throw new ValidationException("Invalid email: " + notification.getRecipient());
        }
        if (notification.getSubject() == null || notification.getSubject().isBlank()) {
            throw new ValidationException("Email subject required");
        }
        if (config.getFrom() == null || !ValidationUtils.isValidEmail(config.getFrom())) {
            throw new ValidationException("Invalid from address");
        }
    }
    
    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.EMAIL;
    }
    
    @Override
    public String getProviderName() {
        return config.getProviderName();
    }
    
    private Map<String, Object> buildEmailRequest(Notification notification) {
        Map<String, Object> request = new HashMap<>();
        request.put("from", config.getFrom());
        request.put("to", notification.getRecipient());
        request.put("subject", notification.getSubject());
        request.put("body", notification.getBody());
        return request;
    }
}
