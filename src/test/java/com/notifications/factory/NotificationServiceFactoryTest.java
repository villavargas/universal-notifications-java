package com.notifications.factory;

import com.notifications.config.NotificationConfig;
import com.notifications.config.ProviderConfig;
import com.notifications.core.*;
import com.notifications.provider.EmailNotificationProvider;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceFactoryTest {

    @Test
    void testCreateWithConfig() {
        ProviderConfig emailConfig = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-key")
                .from("noreply@example.com")
                .enabled(true)
                .build();

        NotificationConfig config = NotificationConfig.builder()
                .provider(NotificationChannel.EMAIL, emailConfig)
                .build();

        NotificationService service = NotificationServiceFactory.create(config);

        assertNotNull(service);
        assertTrue(service.isChannelSupported(NotificationChannel.EMAIL));
    }

    @Test
    void testCreateWithMultipleProviders() {
        ProviderConfig emailConfig = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("email-key")
                .from("noreply@example.com")
                .enabled(true)
                .build();

        ProviderConfig smsConfig = ProviderConfig.builder()
                .channel(NotificationChannel.SMS)
                .providerName("Twilio")
                .apiKey("sms-key")
                .from("+15551234567")
                .enabled(true)
                .build();

        NotificationConfig config = NotificationConfig.builder()
                .provider(NotificationChannel.EMAIL, emailConfig)
                .provider(NotificationChannel.SMS, smsConfig)
                .build();

        NotificationService service = NotificationServiceFactory.create(config);

        assertNotNull(service);
        assertTrue(service.isChannelSupported(NotificationChannel.EMAIL));
        assertTrue(service.isChannelSupported(NotificationChannel.SMS));
    }

    @Test
    void testCreateWithDisabledProvider() {
        ProviderConfig emailConfig = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-key")
                .from("noreply@example.com")
                .enabled(false)
                .build();

        NotificationConfig config = NotificationConfig.builder()
                .provider(NotificationChannel.EMAIL, emailConfig)
                .build();

        // When all providers are disabled, the service should throw an exception
        // because DefaultNotificationService requires at least one provider
        assertThrows(IllegalArgumentException.class, 
                () -> NotificationServiceFactory.create(config));
    }

    @Test
    void testCreateWithNullConfig() {
        assertThrows(IllegalArgumentException.class, 
                () -> NotificationServiceFactory.create((NotificationConfig) null));
    }

    @Test
    void testCreateWithCustomProviders() {
        Map<NotificationChannel, NotificationProvider> providers = new HashMap<>();
        
        ProviderConfig emailConfig = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("TestProvider")
                .apiKey("key")
                .from("test@example.com")
                .build();

        providers.put(NotificationChannel.EMAIL, new EmailNotificationProvider(emailConfig));

        NotificationService service = NotificationServiceFactory.create(providers);

        assertNotNull(service);
        assertTrue(service.isChannelSupported(NotificationChannel.EMAIL));
    }

    @Test
    void testCreateWithEmptyProviders() {
        Map<NotificationChannel, NotificationProvider> emptyProviders = new HashMap<>();
        
        assertThrows(IllegalArgumentException.class, 
                () -> NotificationServiceFactory.create(emptyProviders));
    }

    @Test
    void testCreateWithNullProviders() {
        assertThrows(IllegalArgumentException.class, 
                () -> NotificationServiceFactory.create((Map<NotificationChannel, NotificationProvider>) null));
    }

    @Test
    void testCreateSingleChannel() {
        ProviderConfig emailConfig = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("TestProvider")
                .apiKey("key")
                .from("test@example.com")
                .build();

        EmailNotificationProvider provider = new EmailNotificationProvider(emailConfig);

        NotificationService service = NotificationServiceFactory.createSingleChannel(
                NotificationChannel.EMAIL, provider);

        assertNotNull(service);
        assertTrue(service.isChannelSupported(NotificationChannel.EMAIL));
        assertFalse(service.isChannelSupported(NotificationChannel.SMS));
    }

    @Test
    void testCreateSingleChannelWithNullChannel() {
        ProviderConfig emailConfig = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("TestProvider")
                .apiKey("key")
                .from("test@example.com")
                .build();

        EmailNotificationProvider provider = new EmailNotificationProvider(emailConfig);

        assertThrows(IllegalArgumentException.class, 
                () -> NotificationServiceFactory.createSingleChannel(null, provider));
    }

    @Test
    void testCreateSingleChannelWithNullProvider() {
        assertThrows(IllegalArgumentException.class, 
                () -> NotificationServiceFactory.createSingleChannel(NotificationChannel.EMAIL, null));
    }

    @Test
    void testFactoryCannotBeInstantiated() {
        assertThrows(Exception.class, () -> {
            java.lang.reflect.Constructor<NotificationServiceFactory> constructor = 
                    NotificationServiceFactory.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            try {
                constructor.newInstance();
            } catch (java.lang.reflect.InvocationTargetException e) {
                // Unwrap the InvocationTargetException to check the actual cause
                throw e.getCause();
            }
        });
    }
}
