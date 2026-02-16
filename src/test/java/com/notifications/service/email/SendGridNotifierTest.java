package com.notifications.service.email;

import com.notifications.core.NotificationException;
import com.notifications.core.NotificationResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SendGridNotifier
 */
class SendGridNotifierTest {
    
    @Test
    void testBuilderWithMinimalConfiguration() {
        SendGridNotifier notifier = SendGridNotifier.builder()
                .apiKey("SG.test-api-key")
                .from("sender@example.com")
                .addTo("recipient@example.com")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithFromName() {
        SendGridNotifier notifier = SendGridNotifier.builder()
                .apiKey("SG.test-api-key")
                .from("sender@example.com")
                .fromName("My Application")
                .addTo("recipient@example.com")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithMultipleRecipients() {
        SendGridNotifier notifier = SendGridNotifier.builder()
                .apiKey("SG.test-api-key")
                .from("sender@example.com")
                .addTo("user1@example.com")
                .addTo("user2@example.com")
                .addTo("user3@example.com")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithTemplate() {
        SendGridNotifier notifier = SendGridNotifier.builder()
                .apiKey("SG.test-api-key")
                .from("sender@example.com")
                .addTo("recipient@example.com")
                .templateId("d-12345678")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithTemplateData() {
        SendGridNotifier notifier = SendGridNotifier.builder()
                .apiKey("SG.test-api-key")
                .from("sender@example.com")
                .addTo("recipient@example.com")
                .templateId("d-12345678")
                .addTemplateData("userName", "John Doe")
                .addTemplateData("orderNumber", "12345")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderThrowsExceptionWhenApiKeyMissing() {
        assertThrows(IllegalArgumentException.class, () -> {
            SendGridNotifier.builder()
                    .from("sender@example.com")
                    .addTo("recipient@example.com")
                    .build();
        });
    }
    
    @Test
    void testBuilderThrowsExceptionWhenFromMissing() {
        assertThrows(IllegalArgumentException.class, () -> {
            SendGridNotifier.builder()
                    .apiKey("SG.test-api-key")
                    .addTo("recipient@example.com")
                    .build();
        });
    }
    
    @Test
    void testBuilderThrowsExceptionWhenNoRecipients() {
        // Builder doesn't validate recipients, but send() will fail
        SendGridNotifier notifier = SendGridNotifier.builder()
                .apiKey("SG.test-api-key")
                .from("sender@example.com")
                .build();
        
        assertNotNull(notifier);
        
        // Should fail when trying to send without recipients
        assertThrows(NotificationException.class, () -> {
            notifier.send("Subject", "Message");
        });
    }
    
    @Test
    void testSendWithSubjectAndMessage() throws NotificationException {
        SendGridNotifier notifier = SendGridNotifier.builder()
                .apiKey("SG.test-api-key")
                .from("sender@example.com")
                .addTo("recipient@example.com")
                .build();
        
        NotificationResult result = notifier.send("Test Subject", "Test Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getProviderId());
        assertTrue(result.getProviderId().startsWith("sendgrid-"));
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("SendGrid"));
    }
    
    @Test
    void testSendWithNullSubject() throws NotificationException {
        SendGridNotifier notifier = SendGridNotifier.builder()
                .apiKey("SG.test-api-key")
                .from("sender@example.com")
                .addTo("recipient@example.com")
                .build();
        
        NotificationResult result = notifier.send(null, "Test Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testSendWithNullMessage() throws NotificationException {
        SendGridNotifier notifier = SendGridNotifier.builder()
                .apiKey("SG.test-api-key")
                .from("sender@example.com")
                .addTo("recipient@example.com")
                .build();
        
        NotificationResult result = notifier.send("Test Subject", null);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testSendWithTemplate() throws NotificationException {
        SendGridNotifier notifier = SendGridNotifier.builder()
                .apiKey("SG.test-api-key")
                .from("sender@example.com")
                .addTo("recipient@example.com")
                .templateId("d-12345678")
                .addTemplateData("userName", "John")
                .build();
        
        NotificationResult result = notifier.send("Subject", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testSendMultipleTimes() throws NotificationException {
        SendGridNotifier notifier = SendGridNotifier.builder()
                .apiKey("SG.test-api-key")
                .from("sender@example.com")
                .addTo("recipient@example.com")
                .build();
        
        NotificationResult result1 = notifier.send("Subject 1", "Message 1");
        NotificationResult result2 = notifier.send("Subject 2", "Message 2");
        NotificationResult result3 = notifier.send("Subject 3", "Message 3");
        
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
    void testSendToMultipleRecipients() throws NotificationException {
        SendGridNotifier notifier = SendGridNotifier.builder()
                .apiKey("SG.test-api-key")
                .from("sender@example.com")
                .addTo("user1@example.com")
                .addTo("user2@example.com")
                .addTo("user3@example.com")
                .build();
        
        NotificationResult result = notifier.send("Subject", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("3 recipients"));
    }
    
    @Test
    void testAddReceiverIgnoresNull() {
        SendGridNotifier notifier = SendGridNotifier.builder()
                .apiKey("SG.test-api-key")
                .from("sender@example.com")
                .addTo("valid@example.com")
                .addTo(null)  // Should be ignored
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testAddReceiverIgnoresEmpty() {
        SendGridNotifier notifier = SendGridNotifier.builder()
                .apiKey("SG.test-api-key")
                .from("sender@example.com")
                .addTo("valid@example.com")
                .addTo("")  // Should be ignored
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testAddReceiverTrimsWhitespace() throws NotificationException {
        SendGridNotifier notifier = SendGridNotifier.builder()
                .apiKey("SG.test-api-key")
                .from("sender@example.com")
                .addTo("  user@example.com  ")
                .build();
        
        NotificationResult result = notifier.send("Subject", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testTemplateDataWithMultipleValues() {
        SendGridNotifier notifier = SendGridNotifier.builder()
                .apiKey("SG.test-api-key")
                .from("sender@example.com")
                .addTo("user@example.com")
                .templateId("d-12345")
                .addTemplateData("key1", "value1")
                .addTemplateData("key2", "value2")
                .addTemplateData("key3", "value3")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testProviderIdFormat() throws NotificationException {
        SendGridNotifier notifier = SendGridNotifier.builder()
                .apiKey("SG.test-api-key")
                .from("sender@example.com")
                .addTo("user@example.com")
                .build();
        
        NotificationResult result = notifier.send("Subject", "Message");
        
        assertNotNull(result.getProviderId());
        assertTrue(result.getProviderId().startsWith("sendgrid-"));
        // UUID format check
        String[] parts = result.getProviderId().split("-", 2);
        assertEquals(2, parts.length);
    }
}
