package com.notifications.provider;

import com.notifications.config.ProviderConfig;
import com.notifications.core.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SmsNotificationProvider.
 */
class SmsNotificationProviderTest {

    private SmsNotificationProvider provider;
    private ProviderConfig config;

    @BeforeEach
    void setUp() {
        config = ProviderConfig.builder()
                .channel(NotificationChannel.SMS)
                .providerName("Twilio")
                .apiKey("test-account-sid")
                .apiSecret("test-auth-token")
                .from("+15551234567")
                .enabled(true)
                .build();

        provider = new SmsNotificationProvider(config);
    }

    @Test
    void testGetChannel() {
        assertEquals(NotificationChannel.SMS, provider.getChannel());
    }

    @Test
    void testGetProviderName() {
        assertEquals("Twilio", provider.getProviderName());
    }

    @Test
    void testIsConfigured() {
        assertTrue(provider.isConfigured());
    }

    @Test
    void testSendSmsSuccess() throws NotificationException {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.SMS)
                .recipient("+15559876543")
                .body("Your verification code is: 123456")
                .build();

        NotificationResult result = provider.send(notification);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(NotificationChannel.SMS, result.getChannel());
        assertNotNull(result.getProviderId());
    }

    @Test
    void testSendSmsWithInvalidRecipient() {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.SMS)
                .recipient("invalid-phone")
                .body("Test message")
                .build();

        assertThrows(ValidationException.class, () -> provider.send(notification));
    }

    @Test
    void testSendSmsWithEmptyBody() throws NotificationException {
        // SMS providers typically allow empty body, though not recommended
        Notification notification = Notification.builder()
                .channel(NotificationChannel.SMS)
                .recipient("+15559876543")
                .body("")
                .build();

        NotificationResult result = provider.send(notification);
        assertTrue(result.isSuccess());
    }

    @Test
    void testSendAsync() throws Exception {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.SMS)
                .recipient("+15559876543")
                .body("Async SMS test")
                .build();

        NotificationResult result = provider.sendAsync(notification).get();

        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    void testSendWithTooLongBody() {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.SMS)
                .recipient("+15559876543")
                .body("A".repeat(2000)) // Too long for SMS
                .build();

        assertThrows(ValidationException.class, () -> provider.send(notification));
    }

    @Test
    void testProviderConfigWithNullConfig() {
        assertThrows(IllegalArgumentException.class, 
                () -> new SmsNotificationProvider(null));
    }

    @Test
    void testSendWithShortBody() throws NotificationException {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.SMS)
                .recipient("+15559876543")
                .body("OK")
                .build();

        NotificationResult result = provider.send(notification);
        assertTrue(result.isSuccess());
    }

    @Test
    void testSendWithNullBody() {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.SMS)
                .recipient("+15559876543")
                .body(null)
                .build();

        assertThrows(ValidationException.class, () -> provider.send(notification));
    }

    @Test
    void testSendWithNullRecipient() {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.SMS)
                .recipient(null)
                .body("Test")
                .build();

        assertThrows(ValidationException.class, () -> provider.send(notification));
    }

    @Test
    void testSendWithWrongChannel() {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.EMAIL) // Wrong channel
                .recipient("+15559876543")
                .body("Test")
                .build();

        assertThrows(ValidationException.class, () -> provider.send(notification));
    }
}
