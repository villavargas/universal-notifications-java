package com.notifications.provider;

import com.notifications.config.ProviderConfig;
import com.notifications.core.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PushNotificationProviderTest {

    private PushNotificationProvider provider;

    @BeforeEach
    void setUp() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.PUSH)
                .providerName("Firebase")
                .apiKey("test-firebase-key")
                .from("app-identifier")
                .endpoint("https://fcm.googleapis.com")
                .build();

        provider = new PushNotificationProvider(config);
    }

    @Test
    void testSendPushNotification() throws NotificationException {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.PUSH)
                .recipient("device-token-" + "x".repeat(32)) // Valid device token (min 32 chars)
                .subject("New Message")
                .body("You have a new message")
                .build();

        NotificationResult result = provider.send(notification);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(NotificationChannel.PUSH, result.getChannel());
    }

    @Test
    void testSendWithInvalidRecipient() {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.PUSH)
                .recipient("")
                .body("Test")
                .build();

        assertThrows(ValidationException.class, () -> provider.send(notification));
    }

    @Test
    void testSendAsync() throws Exception {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.PUSH)
                .recipient("device-token-async-" + "y".repeat(32)) // Valid device token
                .body("Async push test")
                .build();

        NotificationResult result = provider.sendAsync(notification).get();

        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    void testGetChannel() {
        assertEquals(NotificationChannel.PUSH, provider.getChannel());
    }

    @Test
    void testGetProviderName() {
        assertEquals("Firebase", provider.getProviderName());
    }

    @Test
    void testIsConfigured() {
        assertTrue(provider.isConfigured());
    }
}
