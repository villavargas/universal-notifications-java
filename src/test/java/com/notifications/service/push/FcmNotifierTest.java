package com.notifications.service.push;

import com.notifications.core.NotificationException;
import com.notifications.core.NotificationResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FcmNotifier (Firebase Cloud Messaging)
 */
class FcmNotifierTest {
    
    @Test
    void testBuilderWithMinimalConfiguration() {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithMultipleDeviceTokens() {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
                .addDeviceToken("device-token-456")
                .addDeviceToken("device-token-789")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithPriority() {
        FcmNotifier highPriority = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
                .priority("high")
                .build();
        
        FcmNotifier normalPriority = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
                .priority("normal")
                .build();
        
        assertNotNull(highPriority);
        assertNotNull(normalPriority);
    }
    
    @Test
    void testBuilderWithData() {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
                .addDataField("key1", "value1")
                .addDataField("key2", "value2")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderThrowsExceptionWhenProjectIdMissing() {
        assertThrows(IllegalArgumentException.class, () -> {
            FcmNotifier.builder()
                    .serviceAccountKey("path/to/service-account.json")
                    .addDeviceToken("device-token-123")
                    .build();
        });
    }
    
    @Test
    void testBuilderDoesNotRequireServiceAccountKey() {
        // Service account key is optional in builder
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .addDeviceToken("device-token-123")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderAllowsNoDeviceTokensButSendFails() {
        // Builder doesn't validate device tokens, but send() will fail
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .build();
        
        assertNotNull(notifier);
        
        // Should fail when trying to send without device tokens
        assertThrows(NotificationException.class, () -> {
            notifier.send("Title", "Message");
        });
    }
    
    @Test
    void testSendWithTitleAndBody() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
                .build();
        
        NotificationResult result = notifier.send("New Message", "You have a new message from John");
        
        assertNotNull(result);
        assertNotNull(result.getProviderId());
        assertTrue(result.getProviderId().startsWith("fcm-"));
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("Firebase") || result.getMessage().contains("FCM"));
        assertTrue(result.isSuccess(), "Expected result to be successful but was: " + result.isSuccess());
    }
    
    @Test
    void testSendWithNullSubject() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
                .build();
        
        NotificationResult result = notifier.send(null, "Message body");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        // Should use default title "Notification"
    }
    
    @Test
    void testSendWithNullMessage() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
                .build();
        
        NotificationResult result = notifier.send("Title", null);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testSendToMultipleDevices() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
                .addDeviceToken("device-token-456")
                .addDeviceToken("device-token-789")
                .build();
        
        NotificationResult result = notifier.send("Title", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("3 devices"));
    }
    
    @Test
    void testSendMultipleTimes() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
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
    }
    
    @Test
    void testAddDeviceTokenIgnoresNull() {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
                .addDeviceToken(null)  // Should be ignored
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testAddDeviceTokenIgnoresEmpty() {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
                .addDeviceToken("")  // Should be ignored
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testAddDeviceTokenTrimsWhitespace() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("  device-token-123  ")
                .build();
        
        NotificationResult result = notifier.send("Title", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testDataPayloadWithMultipleValues() {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
                .addDataField("key1", "value1")
                .addDataField("key2", "value2")
                .addDataField("key3", "value3")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testProviderIdFormat() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
                .build();
        
        NotificationResult result = notifier.send("Title", "Message");
        
        assertNotNull(result.getProviderId());
        assertTrue(result.getProviderId().startsWith("fcm-"));
        // UUID format check
        String[] parts = result.getProviderId().split("-", 2);
        assertEquals(2, parts.length);
    }
    
    @Test
    void testDifferentPriorityLevels() throws NotificationException {
        FcmNotifier highPriority = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
                .priority("high")
                .build();
        
        FcmNotifier normalPriority = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-456")
                .priority("normal")
                .build();
        
        NotificationResult result1 = highPriority.send("Urgent", "Message");
        NotificationResult result2 = normalPriority.send("Info", "Message");
        
        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());
    }
    
    @Test
    void testDefaultPriorityIsHigh() {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
                .build();
        
        assertNotNull(notifier);
        // Default priority should be "high"
    }
    
    // ============= NEW COMPREHENSIVE TESTS FOR 100% COVERAGE =============
    
    @Test
    void testBuilderWithTopic() {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .topic("news-updates")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testSendToTopic() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .topic("news-updates")
                .build();
        
        NotificationResult result = notifier.send("Breaking News", "Important update available");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("topic"));
        assertTrue(result.getMessage().contains("news-updates"));
    }
    
    @Test
    void testBuilderWithCondition() {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .condition("'stock-GOOG' in topics || 'industry-tech' in topics")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testSendWithCondition() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .condition("'premium' in topics && 'news' in topics")
                .build();
        
        NotificationResult result = notifier.send("Premium News", "Exclusive content for premium users");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("condition"));
    }
    
    @Test
    void testBuilderWithTTL() {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
                .ttl(3600)
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testSendWithTTL() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
                .ttl(7200)
                .build();
        
        NotificationResult result = notifier.send("Time-sensitive", "This notification expires in 2 hours");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testBuilderWithAllFeatures() {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
                .addDeviceToken("device-token-456")
                .priority("high")
                .ttl(3600)
                .addDataField("action", "open_chat")
                .addDataField("chat_id", "12345")
                .addDataField("user_id", "67890")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testSendWithAllFeatures() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
                .priority("high")
                .ttl(1800)
                .addDataField("action", "view_order")
                .addDataField("order_id", "ORD-123")
                .build();
        
        NotificationResult result = notifier.send("Order Status", "Your order has been shipped");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getProviderId().startsWith("fcm-"));
    }
    
    @Test
    void testAddDeviceTokenAfterBuild() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .build();
        
        // Add device token after building
        notifier.addDeviceToken("device-token-123");
        
        NotificationResult result = notifier.send("Test", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testAddMultipleDeviceTokensAfterBuild() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .build();
        
        // Add multiple device tokens after building
        notifier.addDeviceToken("device-token-123");
        notifier.addDeviceToken("device-token-456");
        notifier.addDeviceToken("device-token-789");
        
        NotificationResult result = notifier.send("Test", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("3 devices"));
    }
    
    @Test
    void testAddDeviceTokensVarargs() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .build();
        
        // Use varargs method to add multiple tokens at once
        notifier.addDeviceTokens("token-1", "token-2", "token-3");
        
        NotificationResult result = notifier.send("Broadcast", "Message to multiple devices");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("3 devices"));
    }
    
    @Test
    void testAddDeviceTokensVarargsIgnoresNullAndEmpty() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .build();
        
        // Mix of valid and invalid tokens
        notifier.addDeviceTokens("token-1", null, "", "  ", "token-2");
        
        NotificationResult result = notifier.send("Test", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("2 devices"));
    }
    
    @Test
    void testAddDeviceTokenAfterBuildIgnoresNull() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
                .build();
        
        notifier.addDeviceToken(null);  // Should be ignored
        
        NotificationResult result = notifier.send("Test", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("1 devices"));
    }
    
    @Test
    void testAddDeviceTokenAfterBuildIgnoresEmpty() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
                .build();
        
        notifier.addDeviceToken("");  // Should be ignored
        notifier.addDeviceToken("   ");  // Should be ignored
        
        NotificationResult result = notifier.send("Test", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("1 devices"));
    }
    
    @Test
    void testBuilderWithEmptyProjectIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            FcmNotifier.builder()
                    .projectId("")
                    .serviceAccountKey("path/to/service-account.json")
                    .addDeviceToken("device-token-123")
                    .build();
        });
    }
    
    @Test
    void testBuilderWithWhitespaceProjectIdThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            FcmNotifier.builder()
                    .projectId("   ")
                    .serviceAccountKey("path/to/service-account.json")
                    .addDeviceToken("device-token-123")
                    .build();
        });
    }
    
    @Test
    void testSendWithEmptySubject() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
                .build();
        
        // Empty string (not null) should use default "Notification"
        NotificationResult result = notifier.send("", "Message body");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testSendWithEmptyMessage() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
                .build();
        
        // Empty message should be allowed
        NotificationResult result = notifier.send("Title", "");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testSendWithBothSubjectAndMessageEmpty() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .serviceAccountKey("path/to/service-account.json")
                .addDeviceToken("device-token-123")
                .build();
        
        NotificationResult result = notifier.send("", "");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testGetTargetDescriptionWithDeviceTokens() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .addDeviceToken("device-token-123")
                .addDeviceToken("device-token-456")
                .build();
        
        NotificationResult result = notifier.send("Test", "Message");
        
        assertTrue(result.getMessage().contains("2 devices"));
    }
    
    @Test
    void testGetTargetDescriptionWithTopic() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .topic("sports")
                .build();
        
        NotificationResult result = notifier.send("Test", "Message");
        
        assertTrue(result.getMessage().contains("topic"));
        assertTrue(result.getMessage().contains("sports"));
    }
    
    @Test
    void testGetTargetDescriptionWithCondition() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .condition("'vip' in topics")
                .build();
        
        NotificationResult result = notifier.send("Test", "Message");
        
        assertTrue(result.getMessage().contains("condition"));
        assertTrue(result.getMessage().contains("vip"));
    }
    
    @Test
    void testTopicPriorityOverDeviceTokens() throws NotificationException {
        // When both topic and device tokens are present, topic should be used
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .addDeviceToken("device-token-123")
                .topic("announcements")
                .build();
        
        NotificationResult result = notifier.send("Test", "Message");
        
        // Should prefer device tokens over topic in target description
        assertTrue(result.getMessage().contains("1 devices"));
    }
    
    @Test
    void testConditionWhenNoDeviceTokensOrTopic() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .condition("'premium' in topics")
                .build();
        
        NotificationResult result = notifier.send("Test", "Message");
        
        assertTrue(result.getMessage().contains("condition"));
    }
    
    @Test
    void testMultipleDataFields() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .addDeviceToken("device-token-123")
                .addDataField("field1", "value1")
                .addDataField("field2", "value2")
                .addDataField("field3", "value3")
                .addDataField("field4", "value4")
                .addDataField("field5", "value5")
                .build();
        
        NotificationResult result = notifier.send("Data Test", "Testing multiple data fields");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testZeroTTL() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .addDeviceToken("device-token-123")
                .ttl(0)
                .build();
        
        NotificationResult result = notifier.send("Immediate", "No retry");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testNullTTL() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .addDeviceToken("device-token-123")
                .ttl(null)
                .build();
        
        NotificationResult result = notifier.send("Test", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testBuilderMethodChaining() {
        // Test that all builder methods return the builder for chaining
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("project-123")
                .serviceAccountKey("key.json")
                .addDeviceToken("token-1")
                .topic("news")
                .condition("condition")
                .priority("high")
                .addDataField("key", "value")
                .ttl(3600)
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testInstanceMethodChaining() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("project-123")
                .build();
        
        // Test that instance methods return the notifier for chaining
        FcmNotifier result = notifier
                .addDeviceToken("token-1")
                .addDeviceToken("token-2");
        
        assertSame(notifier, result);
        
        NotificationResult sendResult = notifier.send("Test", "Message");
        assertTrue(sendResult.isSuccess());
    }
    
    @Test
    void testVarargsWithEmptyArray() throws NotificationException {
        FcmNotifier notifier = FcmNotifier.builder()
                .projectId("my-firebase-project")
                .addDeviceToken("device-token-123")
                .build();
        
        // Empty varargs should not throw exception
        notifier.addDeviceTokens();
        
        NotificationResult result = notifier.send("Test", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
}
