package com.notifications.notifier;

import com.notifications.config.ProviderConfig;
import com.notifications.core.NotificationChannel;
import com.notifications.core.NotificationException;
import com.notifications.core.NotificationResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PushNotifier
 */
class PushNotifierTest {
    
    @Test
    void testBuilderWithMinimalConfiguration() {
        PushNotifier notifier = PushNotifier.builder()
                .addReceiver("device-token-123")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithMultipleDeviceTokens() {
        PushNotifier notifier = PushNotifier.builder()
                .addReceiver("device-token-123")
                .addReceiver("device-token-456")
                .addReceiver("device-token-789")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithDeviceTokensArray() {
        PushNotifier notifier = PushNotifier.builder()
                .addReceivers("device-token-123", "device-token-456", "device-token-789")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithCustomProvider() {
        PushNotifier notifier = PushNotifier.builder()
                .providerName("APNs")
                .addReceiver("device-token-123")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithPriority() {
        PushNotifier notifier = PushNotifier.builder()
                .priority("normal")
                .addReceiver("device-token-123")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithHighPriority() {
        PushNotifier notifier = PushNotifier.builder()
                .priority("high")
                .addReceiver("device-token-123")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderCanBuildWithoutReceivers() {
        // Builder allows creation without receivers, but send() will fail
        PushNotifier notifier = PushNotifier.builder().build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testFromConfig() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.PUSH)
                .providerName("Firebase")
                .apiKey("test-api-key")
                .property("receivers", "device-token-123,device-token-456")
                .property("priority", "high")
                .build();
        
        PushNotifier notifier = PushNotifier.fromConfig(config);
        
        assertNotNull(notifier);
    }
    
    @Test
    void testFromConfigWithDefaultPriority() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.PUSH)
                .providerName("Firebase")
                .apiKey("test-api-key")
                .property("receivers", "device-token-123")
                .build();
        
        PushNotifier notifier = PushNotifier.fromConfig(config);
        
        assertNotNull(notifier);
    }
    
    @Test
    void testFromConfigWithoutReceivers() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.PUSH)
                .providerName("Firebase")
                .apiKey("test-api-key")
                .build();
        
        PushNotifier notifier = PushNotifier.fromConfig(config);
        
        assertNotNull(notifier);
    }
    
    @Test
    void testSendWithSubjectAndMessage() throws NotificationException {
        PushNotifier notifier = PushNotifier.builder()
                .addReceiver("device-token-123")
                .build();
        
        NotificationResult result = notifier.send("New Message", "You have a new message from John");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(NotificationChannel.PUSH, result.getChannel());
        assertNotNull(result.getProviderId());
        assertTrue(result.getProviderId().startsWith("firebase-"));
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("Push notification sent"));
    }
    
    @Test
    void testSendWithNullSubject() throws NotificationException {
        PushNotifier notifier = PushNotifier.builder()
                .addReceiver("device-token-123")
                .build();
        
        NotificationResult result = notifier.send(null, "Message body");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        // Should use default title "Notification" when subject is null
    }
    
    @Test
    void testSendWithNullMessage() throws NotificationException {
        PushNotifier notifier = PushNotifier.builder()
                .addReceiver("device-token-123")
                .build();
        
        NotificationResult result = notifier.send("Title", null);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        // Should use empty string for body when message is null
    }
    
    @Test
    void testSendWithEmptySubject() throws NotificationException {
        PushNotifier notifier = PushNotifier.builder()
                .addReceiver("device-token-123")
                .build();
        
        NotificationResult result = notifier.send("", "Message body");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testSendWithEmptyMessage() throws NotificationException {
        PushNotifier notifier = PushNotifier.builder()
                .addReceiver("device-token-123")
                .build();
        
        NotificationResult result = notifier.send("Title", "");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testSendThrowsExceptionWhenNoDeviceTokens() {
        PushNotifier notifier = PushNotifier.builder().build();
        
        assertThrows(NotificationException.class, () -> {
            notifier.send("Title", "Message");
        });
    }
    
    @Test
    void testSendWithMultipleDeviceTokens() throws NotificationException {
        PushNotifier notifier = PushNotifier.builder()
                .addReceivers("device-token-123", "device-token-456", "device-token-789")
                .build();
        
        NotificationResult result = notifier.send("Title", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("3 devices"));
    }
    
    @Test
    void testAddReceiverAfterBuild() throws NotificationException {
        PushNotifier notifier = PushNotifier.builder()
                .addReceiver("device-token-123")
                .build();
        
        notifier.addReceiver("device-token-456");
        
        NotificationResult result = notifier.send("Title", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("2 devices"));
    }
    
    @Test
    void testAddReceiversAfterBuild() throws NotificationException {
        PushNotifier notifier = PushNotifier.builder()
                .addReceiver("device-token-123")
                .build();
        
        notifier.addReceivers("device-token-456", "device-token-789");
        
        NotificationResult result = notifier.send("Title", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("3 devices"));
    }
    
    @Test
    void testAddNullDeviceToken() {
        PushNotifier notifier = PushNotifier.builder()
                .addReceiver("device-token-123")
                .build();
        
        // Should not throw exception, just ignore null
        assertDoesNotThrow(() -> notifier.addReceiver(null));
    }
    
    @Test
    void testAddEmptyDeviceToken() {
        PushNotifier notifier = PushNotifier.builder()
                .addReceiver("device-token-123")
                .build();
        
        // Should not throw exception, just ignore empty string
        assertDoesNotThrow(() -> notifier.addReceiver(""));
    }
    
    @Test
    void testAddDeviceTokenWithWhitespace() throws NotificationException {
        PushNotifier notifier = PushNotifier.builder()
                .addReceiver("  device-token-123  ")
                .build();
        
        NotificationResult result = notifier.send("Title", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testChainedAddReceivers() throws NotificationException {
        PushNotifier notifier = PushNotifier.builder()
                .addReceiver("device-token-123")
                .build()
                .addReceiver("device-token-456")
                .addReceiver("device-token-789");
        
        NotificationResult result = notifier.send("Title", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("3 devices"));
    }
    
    @Test
    void testProviderIdFormat() throws NotificationException {
        PushNotifier notifier = PushNotifier.builder()
                .providerName("APNs")
                .addReceiver("device-token-123")
                .build();
        
        NotificationResult result = notifier.send("Title", "Message");
        
        assertNotNull(result.getProviderId());
        assertTrue(result.getProviderId().startsWith("apns-"));
        // UUID format check
        String[] parts = result.getProviderId().split("-", 2);
        assertEquals(2, parts.length);
    }
    
    @Test
    void testResultMessageContainsProviderName() throws NotificationException {
        PushNotifier notifier = PushNotifier.builder()
                .providerName("CustomPushProvider")
                .addReceiver("device-token-123")
                .build();
        
        NotificationResult result = notifier.send("Title", "Message");
        
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("CustomPushProvider"));
    }
    
    @Test
    void testSendMultipleTimes() throws NotificationException {
        PushNotifier notifier = PushNotifier.builder()
                .addReceiver("device-token-123")
                .build();
        
        NotificationResult result1 = notifier.send("Title 1", "Message 1");
        NotificationResult result2 = notifier.send("Title 2", "Message 2");
        NotificationResult result3 = notifier.send("Title 3", "Message 3");
        
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());
        assertTrue(result3.isSuccess());
        
        // Each should have unique provider ID
        assertNotEquals(result1.getProviderId(), result2.getProviderId());
        assertNotEquals(result2.getProviderId(), result3.getProviderId());
        assertNotEquals(result1.getProviderId(), result3.getProviderId());
    }
    
    @Test
    void testDifferentProviders() throws NotificationException {
        PushNotifier firebase = PushNotifier.builder()
                .providerName("Firebase")
                .addReceiver("device-token-123")
                .build();
        
        PushNotifier apns = PushNotifier.builder()
                .providerName("APNs")
                .addReceiver("device-token-456")
                .build();
        
        NotificationResult result1 = firebase.send("Title", "Message");
        NotificationResult result2 = apns.send("Title", "Message");
        
        assertTrue(result1.getProviderId().startsWith("firebase-"));
        assertTrue(result2.getProviderId().startsWith("apns-"));
    }
    
    @Test
    void testPriorityConfiguration() {
        PushNotifier highPriority = PushNotifier.builder()
                .priority("high")
                .addReceiver("device-token-123")
                .build();
        
        PushNotifier normalPriority = PushNotifier.builder()
                .priority("normal")
                .addReceiver("device-token-456")
                .build();
        
        assertNotNull(highPriority);
        assertNotNull(normalPriority);
        // Priority is internal, but we verify it doesn't break the build
    }
    
    @Test
    void testFromConfigWithAllOptions() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.PUSH)
                .providerName("Firebase")
                .apiKey("test-api-key")
                .property("receivers", "token1,token2,token3")
                .property("priority", "normal")
                .build();
        
        PushNotifier notifier = PushNotifier.fromConfig(config);
        
        assertNotNull(notifier);
    }
    
    @Test
    void testFromConfigWithWhitespaceInReceivers() throws NotificationException {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.PUSH)
                .providerName("Firebase")
                .apiKey("test-api-key")
                .property("receivers", " token1 , token2 , token3 ")
                .build();
        
        PushNotifier notifier = PushNotifier.fromConfig(config);
        
        assertNotNull(notifier);
        // Should handle whitespace properly
        NotificationResult result = notifier.send("Title", "Message");
        assertTrue(result.isSuccess());
    }
}
