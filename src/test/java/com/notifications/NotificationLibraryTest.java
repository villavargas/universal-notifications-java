package com.notifications;

import com.notifications.config.NotificationConfig;
import com.notifications.config.ProviderConfig;
import com.notifications.core.*;
import com.notifications.factory.NotificationServiceFactory;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Compilation and basic functionality test for the notification library.
 * This test verifies that the library compiles and can be instantiated.
 */
class NotificationLibraryTest {

    @Test
    void testLibraryCompiles() {
        // This test simply verifies that the library compiles correctly
        assertTrue(true);
    }

    @Test
    void testCreateNotificationService() {
        // Create provider configurations
        Map<NotificationChannel, ProviderConfig> providerConfigs = new HashMap<>();
        
        providerConfigs.put(NotificationChannel.EMAIL, ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-key")
                .enabled(true)
                .build());

        // Create notification service config
        NotificationConfig config = NotificationConfig.builder()
                .providers(providerConfigs)
                .build();

        // Create the service
        NotificationService service = NotificationServiceFactory.create(config);

        // Verify service was created
        assertNotNull(service);
    }

    @Test
    void testCreateNotification() {
        // Create a notification
        Notification notification = Notification.builder()
                .channel(NotificationChannel.EMAIL)
                .recipient("test@example.com")
                .subject("Test")
                .body("Test body")
                .priority(Notification.Priority.NORMAL)
                .build();

        // Verify notification was created
        assertNotNull(notification);
        assertEquals(NotificationChannel.EMAIL, notification.getChannel());
        assertEquals("test@example.com", notification.getRecipient());
        assertEquals("Test", notification.getSubject());
        assertEquals("Test body", notification.getBody());
    }

    @Test
    void testSendNotification() throws NotificationException {
        // Create provider configuration
        Map<NotificationChannel, ProviderConfig> providerConfigs = new HashMap<>();
        
        providerConfigs.put(NotificationChannel.EMAIL, ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-key")
                .endpoint("https://api.example.com")
                .from("noreply@example.com")
                .enabled(true)
                .build());

        // Create service
        NotificationConfig config = NotificationConfig.builder()
                .providers(providerConfigs)
                .build();
        
        NotificationService service = NotificationServiceFactory.create(config);

        // Create notification
        Notification notification = Notification.builder()
                .channel(NotificationChannel.EMAIL)
                .recipient("test@example.com")
                .subject("Test Email")
                .body("This is a test email")
                .build();

        // Send notification
        NotificationResult result = service.send(notification);

        // Verify result
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
}
