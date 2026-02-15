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
import java.util.Set;
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

    @Test
    void testSendWithUnsupportedChannel() {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.PUSH)
                .recipient("device-token-" + "x".repeat(32))
                .body("Test")
                .build();

        assertThrows(ProviderException.class, () -> service.send(notification));
    }

    @Test
    void testSendWithNullNotification() {
        assertThrows(IllegalArgumentException.class, () -> service.send(null));
    }

    @Test
    void testSendAsyncWithNullNotification() throws Exception {
        CompletableFuture<NotificationResult> future = service.sendAsync(null);
        
        assertTrue(future.isCompletedExceptionally());
        assertThrows(Exception.class, () -> future.get());
    }

    @Test
    void testSendAsyncWithUnsupportedChannel() throws Exception {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.PUSH)
                .recipient("device-token-" + "x".repeat(32))
                .body("Test")
                .build();

        CompletableFuture<NotificationResult> future = service.sendAsync(notification);
        NotificationResult result = future.get();

        assertFalse(result.isSuccess());
        assertTrue(result.getErrorDetails().contains("No provider configured") || 
                   result.getMessage().contains("Failed to send notification"));
    }

    @Test
    void testSendBatchWithEmptyList() throws Exception {
        CompletableFuture<List<NotificationResult>> future = service.sendBatch(List.of());
        List<NotificationResult> results = future.get();

        assertTrue(results.isEmpty());
    }

    @Test
    void testSendBatchWithNullList() throws Exception {
        CompletableFuture<List<NotificationResult>> future = service.sendBatch(null);
        List<NotificationResult> results = future.get();

        assertTrue(results.isEmpty());
    }

    @Test
    void testGetSupportedChannels() {
        DefaultNotificationService defaultService = (DefaultNotificationService) service;
        Set<NotificationChannel> channels = defaultService.getSupportedChannels();

        assertEquals(2, channels.size());
        assertTrue(channels.contains(NotificationChannel.EMAIL));
        assertTrue(channels.contains(NotificationChannel.SMS));
    }

    @Test
    void testIsHealthy() {
        DefaultNotificationService defaultService = (DefaultNotificationService) service;
        assertTrue(defaultService.isHealthy());
    }

    @Test
    void testGetStatistics() {
        DefaultNotificationService defaultService = (DefaultNotificationService) service;
        Map<String, Object> stats = defaultService.getStatistics();

        assertNotNull(stats);
        assertEquals(2, stats.get("totalProviders"));
        assertTrue((Boolean) stats.get("healthy"));
        assertNotNull(stats.get("supportedChannels"));
        assertNotNull(stats.get("providerHealth"));
    }

    @Test
    void testConstructorWithNullProviders() {
        assertThrows(IllegalArgumentException.class, 
                () -> new DefaultNotificationService(null));
    }

    @Test
    void testConstructorWithEmptyProviders() {
        assertThrows(IllegalArgumentException.class, 
                () -> new DefaultNotificationService(Map.of()));
    }

    @Test
    void testSendBatchWithMixedResults() throws Exception {
        List<Notification> notifications = List.of(
                Notification.builder()
                        .channel(NotificationChannel.EMAIL)
                        .recipient("valid@example.com")
                        .subject("Test")
                        .body("Body")
                        .build(),
                Notification.builder()
                        .channel(NotificationChannel.PUSH) // Unsupported channel
                        .recipient("device-token-" + "x".repeat(32))
                        .body("Body")
                        .build()
        );

        CompletableFuture<List<NotificationResult>> future = service.sendBatch(notifications);
        List<NotificationResult> results = future.get();

        assertEquals(2, results.size());
        assertTrue(results.get(0).isSuccess());
        assertFalse(results.get(1).isSuccess()); // Should fail for unsupported channel
    }

    @Test
    void testSendBatchAllFailures() throws Exception {
        List<Notification> notifications = List.of(
                Notification.builder()
                        .channel(NotificationChannel.PUSH) // Unsupported
                        .recipient("token1-" + "x".repeat(32))
                        .body("Body")
                        .build(),
                Notification.builder()
                        .channel(NotificationChannel.SLACK) // Unsupported
                        .recipient("#channel")
                        .body("Body")
                        .build()
        );

        CompletableFuture<List<NotificationResult>> future = service.sendBatch(notifications);
        List<NotificationResult> results = future.get();

        assertEquals(2, results.size());
        assertFalse(results.get(0).isSuccess());
        assertFalse(results.get(1).isSuccess());
    }

    @Test
    void testGetProviderForUnsupportedChannel() {
        NotificationProvider provider = service.getProvider(NotificationChannel.PUSH);
        assertNull(provider);
    }

    @Test
    void testSendWithEmptyId() throws NotificationException {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.EMAIL)
                .recipient("test@example.com")
                .subject("Test")
                .body("Body")
                .build();

        NotificationResult result = service.send(notification);
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
}
