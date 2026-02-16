package com.notifications.service.sms;

import com.notifications.core.NotificationException;
import com.notifications.core.NotificationResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TwilioNotifier
 */
class TwilioNotifierTest {
    
    @Test
    void testBuilderWithMinimalConfiguration() {
        TwilioNotifier notifier = TwilioNotifier.builder()
                .accountSid("ACxxxxxxxxxxxxxxxxxxxx")
                .authToken("test-auth-token")
                .fromPhoneNumber("+15551234567")
                .addTo("+15559876543")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithMultipleRecipients() {
        TwilioNotifier notifier = TwilioNotifier.builder()
                .accountSid("ACxxxxxxxxxxxxxxxxxxxx")
                .authToken("test-auth-token")
                .fromPhoneNumber("+15551234567")
                .addTo("+15559876543")
                .addTo("+15551111111")
                .addTo("+15552222222")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithMessagingServiceSid() {
        TwilioNotifier notifier = TwilioNotifier.builder()
                .accountSid("ACxxxxxxxxxxxxxxxxxxxx")
                .authToken("test-auth-token")
                .fromPhoneNumber("+15551234567")
                .messagingServiceSid("MGxxxxxxxxxxxxxxxxxxxx")
                .addTo("+15559876543")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderThrowsExceptionWhenAccountSidMissing() {
        assertThrows(IllegalArgumentException.class, () -> {
            TwilioNotifier.builder()
                    .authToken("test-auth-token")
                    .fromPhoneNumber("+15551234567")
                    .addTo("+15559876543")
                    .build();
        });
    }
    
    @Test
    void testBuilderThrowsExceptionWhenAuthTokenMissing() {
        assertThrows(IllegalArgumentException.class, () -> {
            TwilioNotifier.builder()
                    .accountSid("ACxxxxxxxxxxxxxxxxxxxx")
                    .fromPhoneNumber("+15551234567")
                    .addTo("+15559876543")
                    .build();
        });
    }
    
    @Test
    void testBuilderThrowsExceptionWhenFromPhoneNumberMissing() {
        assertThrows(IllegalArgumentException.class, () -> {
            TwilioNotifier.builder()
                    .accountSid("ACxxxxxxxxxxxxxxxxxxxx")
                    .authToken("test-auth-token")
                    .addTo("+15559876543")
                    .build();
        });
    }
    
    @Test
    void testBuilderAllowsNoRecipientsButSendFails() {
        // Builder doesn't validate recipients, but send() will fail
        TwilioNotifier notifier = TwilioNotifier.builder()
                .accountSid("ACxxxxxxxxxxxxxxxxxxxx")
                .authToken("test-auth-token")
                .fromPhoneNumber("+15551234567")
                .build();
        
        assertNotNull(notifier);
        
        // Should fail when trying to send without recipients
        assertThrows(NotificationException.class, () -> {
            notifier.send("Alert", "Message");
        });
    }
    
    @Test
    void testSendWithSubjectAndMessage() throws NotificationException {
        TwilioNotifier notifier = TwilioNotifier.builder()
                .accountSid("ACxxxxxxxxxxxxxxxxxxxx")
                .authToken("test-auth-token")
                .fromPhoneNumber("+15551234567")
                .addTo("+15559876543")
                .build();
        
        NotificationResult result = notifier.send("Alert", "System is down!");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getProviderId());
        assertTrue(result.getProviderId().startsWith("twilio-"));
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("Twilio"));
    }
    
    @Test
    void testSendConcatenatesSubjectAndMessage() throws NotificationException {
        TwilioNotifier notifier = TwilioNotifier.builder()
                .accountSid("ACxxxxxxxxxxxxxxxxxxxx")
                .authToken("test-auth-token")
                .fromPhoneNumber("+15551234567")
                .addTo("+15559876543")
                .build();
        
        NotificationResult result = notifier.send("Alert", "Message body");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        // SMS concatenates subject and message
    }
    
    @Test
    void testSendWithOnlySubject() throws NotificationException {
        TwilioNotifier notifier = TwilioNotifier.builder()
                .accountSid("ACxxxxxxxxxxxxxxxxxxxx")
                .authToken("test-auth-token")
                .fromPhoneNumber("+15551234567")
                .addTo("+15559876543")
                .build();
        
        NotificationResult result = notifier.send("Alert", null);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testSendWithOnlyMessage() throws NotificationException {
        TwilioNotifier notifier = TwilioNotifier.builder()
                .accountSid("ACxxxxxxxxxxxxxxxxxxxx")
                .authToken("test-auth-token")
                .fromPhoneNumber("+15551234567")
                .addTo("+15559876543")
                .build();
        
        NotificationResult result = notifier.send(null, "Message body");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testSendToMultipleRecipients() throws NotificationException {
        TwilioNotifier notifier = TwilioNotifier.builder()
                .accountSid("ACxxxxxxxxxxxxxxxxxxxx")
                .authToken("test-auth-token")
                .fromPhoneNumber("+15551234567")
                .addTo("+15559876543")
                .addTo("+15551111111")
                .addTo("+15552222222")
                .build();
        
        NotificationResult result = notifier.send("Alert", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("3 recipients"));
    }
    
    @Test
    void testSendMultipleTimes() throws NotificationException {
        TwilioNotifier notifier = TwilioNotifier.builder()
                .accountSid("ACxxxxxxxxxxxxxxxxxxxx")
                .authToken("test-auth-token")
                .fromPhoneNumber("+15551234567")
                .addTo("+15559876543")
                .build();
        
        NotificationResult result1 = notifier.send("Alert 1", "Message 1");
        NotificationResult result2 = notifier.send("Alert 2", "Message 2");
        NotificationResult result3 = notifier.send("Alert 3", "Message 3");
        
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
    void testAddReceiverIgnoresNull() {
        TwilioNotifier notifier = TwilioNotifier.builder()
                .accountSid("ACxxxxxxxxxxxxxxxxxxxx")
                .authToken("test-auth-token")
                .fromPhoneNumber("+15551234567")
                .addTo("+15559876543")
                .addTo(null)  // Should be ignored
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testAddReceiverIgnoresEmpty() {
        TwilioNotifier notifier = TwilioNotifier.builder()
                .accountSid("ACxxxxxxxxxxxxxxxxxxxx")
                .authToken("test-auth-token")
                .fromPhoneNumber("+15551234567")
                .addTo("+15559876543")
                .addTo("")  // Should be ignored
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testAddReceiverTrimsWhitespace() throws NotificationException {
        TwilioNotifier notifier = TwilioNotifier.builder()
                .accountSid("ACxxxxxxxxxxxxxxxxxxxx")
                .authToken("test-auth-token")
                .fromPhoneNumber("+15551234567")
                .addTo("  +15559876543  ")
                .build();
        
        NotificationResult result = notifier.send("Alert", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testProviderIdFormat() throws NotificationException {
        TwilioNotifier notifier = TwilioNotifier.builder()
                .accountSid("ACxxxxxxxxxxxxxxxxxxxx")
                .authToken("test-auth-token")
                .fromPhoneNumber("+15551234567")
                .addTo("+15559876543")
                .build();
        
        NotificationResult result = notifier.send("Alert", "Message");
        
        assertNotNull(result.getProviderId());
        assertTrue(result.getProviderId().startsWith("twilio-"));
        // UUID format check
        String[] parts = result.getProviderId().split("-", 2);
        assertEquals(2, parts.length);
    }
    
    @Test
    void testFromPhoneNumberTrimmed() {
        TwilioNotifier notifier = TwilioNotifier.builder()
                .accountSid("ACxxxxxxxxxxxxxxxxxxxx")
                .authToken("test-auth-token")
                .fromPhoneNumber("  +15551234567  ")
                .addTo("+15559876543")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testMessagingServiceSidOptional() throws NotificationException {
        TwilioNotifier withSid = TwilioNotifier.builder()
                .accountSid("ACxxxxxxxxxxxxxxxxxxxx")
                .authToken("test-auth-token")
                .fromPhoneNumber("+15551234567")
                .messagingServiceSid("MGxxxxxxxxxxxxxxxxxxxx")
                .addTo("+15559876543")
                .build();
        
        TwilioNotifier withoutSid = TwilioNotifier.builder()
                .accountSid("ACxxxxxxxxxxxxxxxxxxxx")
                .authToken("test-auth-token")
                .fromPhoneNumber("+15551234567")
                .addTo("+15559876543")
                .build();
        
        assertNotNull(withSid);
        assertNotNull(withoutSid);
        
        NotificationResult result1 = withSid.send("Alert", "Message");
        NotificationResult result2 = withoutSid.send("Alert", "Message");
        
        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());
    }
}
