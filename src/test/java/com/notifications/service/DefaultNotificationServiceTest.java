package com.notifications.service;

import com.notifications.config.NotificationConfig;
import com.notifications.config.ProviderConfig;
import com.notifications.core.*;
import com.notifications.factory.NotificationServiceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class DefaultNotificationServiceTest {

    private NotificationService service;

    @BeforeEach
    void setUp() {
        Map<NotificationChannel, ProviderConfig> providers = new HashMap<>();
        
        providers.put(NotificationChannel.EMAIL, ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-key")
                .from("noreply@example.com")
                .build());

        providers.put(NotificationChannel.SMS, ProviderConfig.builder()
                .channel(NotificationChannel.SMS)
                .providerName("Twilio")
                .apiKey("test-key")
                .from("+15551234567")
                .build());

        NotificationConfig config = NotificationConfig.builder()
                .providers(providers)
                .build();

        service = NotificationServiceFactory.create(config);
    }

    @Test
    void testSendEmail() throws NotificationException {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.EMAIL)
                .recipient("test@example.com")
                .subject("Test")
                .body("Body")
                .build();

        NotificationResult result = service.send(notification);
        assertTrue(result.isSuccess());
    }

    @Test
    void testSendSms() throws NotificationException {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.SMS)
                .recipient("+15559876543")
                .body("Test SMS")
                .build();

        NotificationResult result = service.send(notification);
        assertTrue(result.isSuccess());
    }

    @Test
    void testSendAsync() throws Exception {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.EMAIL)
                .recipient("async@example.com")
                .subject("Async Test")
                .body("Body")
                .build();

        CompletableFuture<NotificationResult> future = service.sendAsync(notification);
        NotificationResult result = future.get();
        
        assertTrue(result.isSuccess());
    }

    @Test
    void testSendBatch() throws Exception {
        List<Notification> notifications = List.of(
                Notification.builder()
                        .channel(NotificationChannel.EMAIL)
                        .recipient("user1@example.com")
                        .subject("Test 1")
                        .body("Body 1")
                        .build(),
                Notification.builder()
                        .channel(NotificationChannel.SMS)
                        .recipient("+15551111111")
                        .body("SMS 1")
                        .build()
        );

        CompletableFuture<List<NotificationResult>> future = service.sendBatch(notifications);
        List<NotificationResult> results = future.get();

        assertEquals(2, results.size());
        assertTrue(results.get(0).isSuccess());
        assertTrue(results.get(1).isSuccess());
    }

    @Test
    void testGetProvider() {
        NotificationProvider emailProvider = service.getProvider(NotificationChannel.EMAIL);
        assertNotNull(emailProvider);
        assertEquals("SendGrid", emailProvider.getProviderName());
    }

    @Test
    void testIsChannelSupported() {
        assertTrue(service.isChannelSupported(NotificationChannel.EMAIL));
        assertTrue(service.isChannelSupported(NotificationChannel.SMS));
        assertFalse(service.isChannelSupported(NotificationChannel.PUSH));
    }
}
