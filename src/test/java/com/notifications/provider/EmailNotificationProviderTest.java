package com.notifications.provider;

import com.notifications.config.ProviderConfig;
import com.notifications.core.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailNotificationProviderTest {

    private EmailNotificationProvider provider;

    @BeforeEach
    void setUp() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-sendgrid-key")
                .from("noreply@example.com")
                .endpoint("https://api.sendgrid.com")
                .build();

        provider = new EmailNotificationProvider(config);
    }

    @Test
    void testSendEmail() throws NotificationException {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.EMAIL)
                .recipient("test@example.com")
                .subject("Test Subject")
                .body("Test email body")
                .build();

        NotificationResult result = provider.send(notification);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(NotificationChannel.EMAIL, result.getChannel());
        assertTrue(result.getMessage().contains("SendGrid"));
    }

    @Test
    void testSendWithInvalidEmail() {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.EMAIL)
                .recipient("invalid-email")
                .subject("Test")
                .body("Body")
                .build();

        assertThrows(ValidationException.class, () -> provider.send(notification));
    }

    @Test
    void testSendWithMissingSubject() {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.EMAIL)
                .recipient("test@example.com")
                .subject("")
                .body("Body")
                .build();

        assertThrows(ValidationException.class, () -> provider.send(notification));
    }

    @Test
    void testSendWithNullSubject() {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.EMAIL)
                .recipient("test@example.com")
                .subject(null)
                .body("Body")
                .build();

        assertThrows(ValidationException.class, () -> provider.send(notification));
    }

    @Test
    void testSendAsync() throws Exception {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.EMAIL)
                .recipient("async@example.com")
                .subject("Async Test")
                .body("Async body")
                .build();

        NotificationResult result = provider.sendAsync(notification).get();

        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    void testGetChannel() {
        assertEquals(NotificationChannel.EMAIL, provider.getChannel());
    }

    @Test
    void testGetProviderName() {
        assertEquals("SendGrid", provider.getProviderName());
    }

    @Test
    void testIsConfigured() {
        assertTrue(provider.isConfigured());
    }

    @Test
    void testProviderWithInvalidFromAddress() {
        ProviderConfig badConfig = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-key")
                .from("invalid-email")
                .build();

        EmailNotificationProvider badProvider = new EmailNotificationProvider(badConfig);

        Notification notification = Notification.builder()
                .channel(NotificationChannel.EMAIL)
                .recipient("test@example.com")
                .subject("Test")
                .body("Body")
                .build();

        assertThrows(ValidationException.class, () -> badProvider.send(notification));
    }

    @Test
    void testSendWithLongSubject() {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.EMAIL)
                .recipient("test@example.com")
                .subject("A".repeat(300)) // Very long subject
                .body("Body")
                .build();

        // Should handle long subjects gracefully
        assertDoesNotThrow(() -> provider.send(notification));
    }

    @Test
    void testSendWithSpecialCharactersInBody() throws NotificationException {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.EMAIL)
                .recipient("test@example.com")
                .subject("Test")
                .body("Body with special chars: <>&\"'")
                .build();

        NotificationResult result = provider.send(notification);
        assertTrue(result.isSuccess());
    }

    @Test
    void testProviderConfigWithNullConfig() {
        assertThrows(IllegalArgumentException.class, 
                () -> new EmailNotificationProvider(null));
    }

    @Test
    void testSendWithNullBody() {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.EMAIL)
                .recipient("test@example.com")
                .subject("Test")
                .body(null)
                .build();

        assertThrows(ValidationException.class, () -> provider.send(notification));
    }

    @Test
    void testSendWithNullRecipient() {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.EMAIL)
                .recipient(null)
                .subject("Test")
                .body("Body")
                .build();

        assertThrows(ValidationException.class, () -> provider.send(notification));
    }

    @Test
    void testSendWithWrongChannel() {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.SMS) // Wrong channel
                .recipient("test@example.com")
                .subject("Test")
                .body("Body")
                .build();

        assertThrows(ValidationException.class, () -> provider.send(notification));
    }
}
