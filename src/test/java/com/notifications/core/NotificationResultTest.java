package com.notifications.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Map;

class NotificationResultTest {

    @Test
    void testSuccessResult() {
        NotificationResult result = NotificationResult.success(
                "notif-123",
                NotificationChannel.EMAIL,
                "provider-id-456"
        );

        assertTrue(result.isSuccess());
        assertEquals("notif-123", result.getNotificationId());
        assertEquals(NotificationChannel.EMAIL, result.getChannel());
        assertEquals("provider-id-456", result.getProviderId());
        assertNotNull(result.getTimestamp());
        assertEquals("Notification sent successfully", result.getMessage());
        assertNull(result.getMetadata());
    }

    @Test
    void testFailureResult() {
        NotificationResult result = NotificationResult.failure(
                "notif-123",
                NotificationChannel.SMS,
                "Error message"
        );

        assertFalse(result.isSuccess());
        assertEquals("notif-123", result.getNotificationId());
        assertEquals(NotificationChannel.SMS, result.getChannel());
        assertEquals("Failed to send notification", result.getMessage());
        assertEquals("Error message", result.getErrorDetails());
        assertNotNull(result.getTimestamp());
    }

    @Test
    void testBuilderWithAllFields() {
        Instant now = Instant.now();
        Map<String, Object> metadata = Map.of("key", "value");

        NotificationResult result = NotificationResult.builder()
                .notificationId("notif-789")
                .success(true)
                .channel(NotificationChannel.PUSH)
                .providerId("provider-999")
                .message("Success message")
                .timestamp(now)
                .metadata(metadata)
                .build();

        assertTrue(result.isSuccess());
        assertEquals("notif-789", result.getNotificationId());
        assertEquals(NotificationChannel.PUSH, result.getChannel());
        assertEquals("provider-999", result.getProviderId());
        assertEquals("Success message", result.getMessage());
        assertEquals(now, result.getTimestamp());
        assertEquals(metadata, result.getMetadata());
    }
}
