package com.notifications.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

class NotificationTest {

    @Test
    void testNotificationBuilder() {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.EMAIL)
                .recipient("test@example.com")
                .subject("Test Subject")
                .body("Test Body")
                .priority(Notification.Priority.HIGH)
                .build();

        assertNotNull(notification);
        assertEquals(NotificationChannel.EMAIL, notification.getChannel());
        assertEquals("test@example.com", notification.getRecipient());
        assertEquals("Test Subject", notification.getSubject());
        assertEquals("Test Body", notification.getBody());
        assertEquals(Notification.Priority.HIGH, notification.getPriority());
        assertNotNull(notification.getCreatedAt());
    }

    @Test
    void testNotificationWithMetadata() {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.SMS)
                .recipient("+1234567890")
                .body("Test")
                .metadataEntry("key1", "value1")
                .metadataEntry("key2", 123)
                .build();

        assertNotNull(notification.getMetadata());
        assertEquals("value1", notification.getMetadata().get("key1"));
        assertEquals(123, notification.getMetadata().get("key2"));
    }

    @Test
    void testDefaultPriority() {
        Notification notification = Notification.builder()
                .channel(NotificationChannel.PUSH)
                .recipient("device-token")
                .body("Test")
                .build();

        assertEquals(Notification.Priority.NORMAL, notification.getPriority());
    }

    @Test
    void testAllPriorities() {
        for (Notification.Priority priority : Notification.Priority.values()) {
            Notification notification = Notification.builder()
                    .channel(NotificationChannel.EMAIL)
                    .recipient("test@example.com")
                    .body("Test")
                    .priority(priority)
                    .build();

            assertEquals(priority, notification.getPriority());
        }
    }
}
