package com.notifications.provider;

import com.notifications.config.ProviderConfig;
import com.notifications.core.*;
import com.notifications.util.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SmsNotificationProvider extends AbstractNotificationProvider {
    
    private static final int MAX_SMS_LENGTH = 1600;
    
    public SmsNotificationProvider(ProviderConfig config) {
        super(config);
        log.info("Initialized {} SMS provider", getProviderName());
    }
    
    @Override
    protected NotificationResult doSend(Notification notification) throws NotificationException {
        log.debug("Sending SMS - To: {}, Length: {}", notification.getRecipient(), notification.getBody().length());
        
        simulateNetworkLatency();
        String providerId = generateProviderId();
        
        Map<String, Object> request = buildSmsRequest(notification);
        log.info("Simulated SMS request: {}", request);
        
        if (Math.random() < 0.03) {
            throw new ProviderException("Simulated error: Invalid phone number");
        }
        
        int segments = calculateSmsSegments(notification.getBody());
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("segments", segments);
        
        return NotificationResult.builder()
                .notificationId(notification.getId())
                .success(true)
                .channel(NotificationChannel.SMS)
                .providerId(providerId)
                .message(String.format("SMS sent (%d segments)", segments))
                .metadata(metadata)
                .build();
    }
    
    @Override
    protected void validateProviderSpecific(Notification notification) throws ValidationException {
        if (!ValidationUtils.isValidPhoneNumber(notification.getRecipient())) {
            throw new ValidationException("Invalid phone: " + notification.getRecipient());
        }
        if (notification.getBody().length() > MAX_SMS_LENGTH) {
            throw new ValidationException("SMS too long");
        }
    }
    
    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.SMS;
    }
    
    @Override
    public String getProviderName() {
        return config.getProviderName();
    }
    
    private Map<String, Object> buildSmsRequest(Notification notification) {
        Map<String, Object> request = new HashMap<>();
        request.put("from", config.getFrom());
        request.put("to", notification.getRecipient());
        request.put("body", notification.getBody());
        return request;
    }
    
    private int calculateSmsSegments(String body) {
        int length = body.length();
        boolean containsUnicode = !body.matches("^[\\x00-\\x7F]*$");
        
        if (containsUnicode) {
            return length <= 70 ? 1 : (int) Math.ceil((double) length / 67);
        } else {
            return length <= 160 ? 1 : (int) Math.ceil((double) length / 153);
        }
    }
}
