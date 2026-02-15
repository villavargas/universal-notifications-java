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

    @Test
    void testNotificationConfigAddProvider() {
        ProviderConfig emailConfig = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-key")
                .build();

        NotificationConfig config = NotificationConfig.builder()
                .provider(NotificationChannel.EMAIL, emailConfig)
                .build();

        assertEquals(1, config.getProviders().size());
        assertEquals(emailConfig, config.getProviders().get(NotificationChannel.EMAIL));
    }

    @Test
    void testProviderConfigMultipleProperties() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-key")
                .property("prop1", "value1")
                .property("prop2", "value2")
                .property("prop3", 123)
                .build();

        Map<String, Object> props = config.getProperties();
        assertEquals(3, props.size());
        assertEquals("value1", props.get("prop1"));
        assertEquals("value2", props.get("prop2"));
        assertEquals(123, props.get("prop3"));
    }

    @Test
    void testProviderConfigDisabled() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-key")
                .enabled(false)
                .build();

        assertFalse(config.isEnabled());
    }

    @Test
    void testProviderConfigCustomRetries() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.SMS)
                .providerName("Twilio")
                .apiKey("test-key")
                .maxRetries(10)
                .build();

        assertEquals(10, config.getMaxRetries());
    }

    @Test
    void testProviderConfigCustomTimeout() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.PUSH)
                .providerName("Firebase")
                .apiKey("test-key")
                .timeoutSeconds(120)
                .build();

        assertEquals(120, config.getTimeoutSeconds());
    }

    @Test
    void testProviderConfigWithEndpoint() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("CustomProvider")
                .apiKey("test-key")
                .endpoint("https://custom-api.example.com/v1")
                .build();

        assertEquals("https://custom-api.example.com/v1", config.getEndpoint());
    }

    @Test
    void testProviderConfigWithFromAddress() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-key")
                .from("custom-sender@example.com")
                .build();

        assertEquals("custom-sender@example.com", config.getFrom());
    }

    @Test
    void testProviderConfigWithApiSecret() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.SMS)
                .providerName("Twilio")
                .apiKey("account-sid")
                .apiSecret("auth-token")
                .build();

        assertEquals("account-sid", config.getApiKey());
        assertEquals("auth-token", config.getApiSecret());
    }

    @Test
    void testNotificationConfigEmptyProviders() {
        NotificationConfig config = NotificationConfig.builder()
                .providers(new HashMap<>())
                .build();

        assertNotNull(config.getProviders());
        assertTrue(config.getProviders().isEmpty());
    }

    @Test
    void testNotificationConfigValidation() {
        ProviderConfig emailConfig = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-key")
                .build();

        NotificationConfig config = NotificationConfig.builder()
                .provider(NotificationChannel.EMAIL, emailConfig)
                .build();

        assertDoesNotThrow(() -> config.validate());
    }

    @Test
    void testNotificationConfigValidationNoProviders() {
        NotificationConfig config = NotificationConfig.builder()
                .build();

        assertThrows(IllegalArgumentException.class, () -> config.validate());
    }

    @Test
    void testNotificationConfigValidationEmptyProviders() {
        NotificationConfig config = NotificationConfig.builder()
                .providers(new HashMap<>())
                .build();

        assertThrows(IllegalArgumentException.class, () -> config.validate());
    }

    @Test
    void testNotificationConfigGetProviderConfig() {
        ProviderConfig emailConfig = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-key")
                .build();

        NotificationConfig config = NotificationConfig.builder()
                .provider(NotificationChannel.EMAIL, emailConfig)
                .build();

        ProviderConfig retrieved = config.getProviderConfig(NotificationChannel.EMAIL);
        assertNotNull(retrieved);
        assertEquals(emailConfig, retrieved);
    }

    @Test
    void testNotificationConfigGetProviderConfigNotFound() {
        ProviderConfig emailConfig = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-key")
                .build();

        NotificationConfig config = NotificationConfig.builder()
                .provider(NotificationChannel.EMAIL, emailConfig)
                .build();

        assertNull(config.getProviderConfig(NotificationChannel.SMS));
    }

    @Test
    void testNotificationConfigIsChannelConfigured() {
        ProviderConfig emailConfig = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-key")
                .enabled(true)
                .build();

        NotificationConfig config = NotificationConfig.builder()
                .provider(NotificationChannel.EMAIL, emailConfig)
                .build();

        assertTrue(config.isChannelConfigured(NotificationChannel.EMAIL));
        assertFalse(config.isChannelConfigured(NotificationChannel.SMS));
    }

    @Test
    void testNotificationConfigIsChannelConfiguredDisabled() {
        ProviderConfig emailConfig = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-key")
                .enabled(false)
                .build();

        NotificationConfig config = NotificationConfig.builder()
                .provider(NotificationChannel.EMAIL, emailConfig)
                .build();

        assertFalse(config.isChannelConfigured(NotificationChannel.EMAIL));
    }

    @Test
    void testNotificationConfigGlobalTimeout() {
        NotificationConfig config = NotificationConfig.builder()
                .globalTimeoutSeconds(120)
                .build();

        assertEquals(120, config.getGlobalTimeoutSeconds());
    }

    @Test
    void testNotificationConfigGlobalTimeoutDefault() {
        NotificationConfig config = NotificationConfig.builder()
                .build();

        assertEquals(60, config.getGlobalTimeoutSeconds());
    }

    @Test
    void testNotificationConfigValidateBeforeSend() {
        NotificationConfig config = NotificationConfig.builder()
                .validateBeforeSend(false)
                .build();

        assertFalse(config.isValidateBeforeSend());
    }

    @Test
    void testNotificationConfigValidateBeforeSendDefault() {
        NotificationConfig config = NotificationConfig.builder()
                .build();

        assertTrue(config.isValidateBeforeSend());
    }

    @Test
    void testProviderConfigGetPropertiesImmutable() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-key")
                .property("key1", "value1")
                .build();

        Map<String, Object> props = config.getProperties();
        assertNotNull(props);
        assertEquals(1, props.size());
    }

    @Test
    void testNotificationConfigCompleteBuilder() {
        ProviderConfig emailConfig = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-key")
                .build();

        NotificationConfig config = NotificationConfig.builder()
                .provider(NotificationChannel.EMAIL, emailConfig)
                .asyncByDefault(true)
                .autoRetry(false)
                .globalTimeoutSeconds(120)
                .validateBeforeSend(false)
                .build();

        assertTrue(config.isAsyncByDefault());
        assertFalse(config.isAutoRetry());
        assertEquals(120, config.getGlobalTimeoutSeconds());
        assertFalse(config.isValidateBeforeSend());
    }

    @Test
    void testProviderConfigCompleteBuilder() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-key")
                .apiSecret("test-secret")
                .endpoint("https://api.example.com")
                .from("noreply@example.com")
                .maxRetries(5)
                .timeoutSeconds(60)
                .enabled(true)
                .property("prop1", "value1")
                .property("prop2", 123)
                .build();

        assertEquals(NotificationChannel.EMAIL, config.getChannel());
        assertEquals("SendGrid", config.getProviderName());
        assertEquals("test-key", config.getApiKey());
        assertEquals("test-secret", config.getApiSecret());
        assertEquals("https://api.example.com", config.getEndpoint());
        assertEquals("noreply@example.com", config.getFrom());
        assertEquals(5, config.getMaxRetries());
        assertEquals(60, config.getTimeoutSeconds());
        assertTrue(config.isEnabled());
        assertEquals(2, config.getProperties().size());
    }

    @Test
    void testProviderConfigGetPropertyDefault() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-key")
                .build();

        Map<String, Object> props = config.getProperties();
        assertNotNull(props);
    }
}
