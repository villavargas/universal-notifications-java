package com.notifications.config;

import com.notifications.core.NotificationChannel;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for configuration classes.
 */
class ConfigTest {

    @Test
    void testProviderConfigBuilder() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-key")
                .apiSecret("test-secret")
                .endpoint("https://api.sendgrid.com")
                .from("noreply@example.com")
                .maxRetries(5)
                .timeoutSeconds(60)
                .enabled(true)
                .property("templateId", "template-123")
                .build();

        assertEquals(NotificationChannel.EMAIL, config.getChannel());
        assertEquals("SendGrid", config.getProviderName());
        assertEquals("test-key", config.getApiKey());
        assertEquals("test-secret", config.getApiSecret());
        assertEquals("https://api.sendgrid.com", config.getEndpoint());
        assertEquals("noreply@example.com", config.getFrom());
        assertEquals(5, config.getMaxRetries());
        assertEquals(60, config.getTimeoutSeconds());
        assertTrue(config.isEnabled());
        assertEquals("template-123", config.getProperties().get("templateId"));
    }

    @Test
    void testProviderConfigDefaults() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-key")
                .build();

        assertEquals(3, config.getMaxRetries()); // Default
        assertEquals(30, config.getTimeoutSeconds()); // Default
        assertTrue(config.isEnabled()); // Default
    }

    @Test
    void testProviderConfigValidation() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-key")
                .build();

        assertDoesNotThrow(() -> config.validate());
    }

    @Test
    void testProviderConfigValidationMissingChannel() {
        ProviderConfig config = ProviderConfig.builder()
                .providerName("SendGrid")
                .apiKey("test-key")
                .build();

        assertThrows(IllegalArgumentException.class, () -> config.validate());
    }

    @Test
    void testProviderConfigValidationMissingProviderName() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .apiKey("test-key")
                .build();

        assertThrows(IllegalArgumentException.class, () -> config.validate());
    }

    @Test
    void testProviderConfigValidationMissingApiKey() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .build();

        assertThrows(IllegalArgumentException.class, () -> config.validate());
    }

    @Test
    void testNotificationConfigBuilder() {
        Map<NotificationChannel, ProviderConfig> providers = new HashMap<>();
        providers.put(NotificationChannel.EMAIL, ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-key")
                .build());

        NotificationConfig config = NotificationConfig.builder()
                .providers(providers)
                .asyncByDefault(true)
                .autoRetry(false)
                .build();

        assertNotNull(config.getProviders());
        assertEquals(1, config.getProviders().size());
        assertTrue(config.isAsyncByDefault());
        assertFalse(config.isAutoRetry());
    }

    @Test
    void testNotificationConfigDefaults() {
        NotificationConfig config = NotificationConfig.builder()
                .build();

        assertFalse(config.isAsyncByDefault()); // Default
        assertTrue(config.isAutoRetry()); // Default
    }

    @Test
    void testNotificationConfigWithMultipleProviders() {
        Map<NotificationChannel, ProviderConfig> providers = new HashMap<>();
        
        providers.put(NotificationChannel.EMAIL, ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("email-key")
                .build());
        
        providers.put(NotificationChannel.SMS, ProviderConfig.builder()
                .channel(NotificationChannel.SMS)
                .providerName("Twilio")
                .apiKey("sms-key")
                .build());

        NotificationConfig config = NotificationConfig.builder()
                .providers(providers)
                .build();

        assertEquals(2, config.getProviders().size());
        assertNotNull(config.getProviders().get(NotificationChannel.EMAIL));
        assertNotNull(config.getProviders().get(NotificationChannel.SMS));
    }
}
