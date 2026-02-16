package com.notifications.notifier;

import com.notifications.config.ProviderConfig;
import com.notifications.core.NotificationChannel;
import com.notifications.core.NotificationException;
import com.notifications.core.NotificationResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SmsNotifier
 */
class SmsNotifierTest {
    
    @Test
    void testBuilderWithMinimalConfiguration() {
        SmsNotifier notifier = SmsNotifier.builder()
                .fromPhoneNumber("+15551234567")
                .addReceiver("+15559876543")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithMultipleReceivers() {
        SmsNotifier notifier = SmsNotifier.builder()
                .fromPhoneNumber("+15551234567")
                .addReceiver("+15559876543")
                .addReceiver("+15551111111")
                .addReceiver("+15552222222")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithReceiversArray() {
        SmsNotifier notifier = SmsNotifier.builder()
                .fromPhoneNumber("+15551234567")
                .addReceivers("+15559876543", "+15551111111", "+15552222222")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithCustomProvider() {
        SmsNotifier notifier = SmsNotifier.builder()
                .providerName("Plivo")
                .fromPhoneNumber("+15551234567")
                .addReceiver("+15559876543")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderThrowsExceptionWhenFromPhoneNumberMissing() {
        assertThrows(IllegalArgumentException.class, () -> {
            SmsNotifier.builder()
                    .addReceiver("+15559876543")
                    .build();
        });
    }
    
    @Test
    void testBuilderThrowsExceptionWhenFromPhoneNumberEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            SmsNotifier.builder()
                    .fromPhoneNumber("")
                    .addReceiver("+15559876543")
                    .build();
        });
    }
    
    @Test
    void testFromConfig() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.SMS)
                .providerName("Twilio")
                .apiKey("test-api-key")
                .property("fromPhoneNumber", "+15551234567")
                .property("receivers", "+15559876543,+15551111111")
                .build();
        
        SmsNotifier notifier = SmsNotifier.fromConfig(config);
        
        assertNotNull(notifier);
    }
    
    @Test
    void testFromConfigWithDefaultFromNumber() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.SMS)
                .providerName("Twilio")
                .apiKey("test-api-key")
                .property("receivers", "+15559876543")
                .build();
        
        SmsNotifier notifier = SmsNotifier.fromConfig(config);
        
        assertNotNull(notifier);
    }
    
    @Test
    void testFromConfigWithoutReceivers() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.SMS)
                .providerName("Twilio")
                .apiKey("test-api-key")
                .property("fromPhoneNumber", "+15551234567")
                .build();
        
        SmsNotifier notifier = SmsNotifier.fromConfig(config);
        
        assertNotNull(notifier);
    }
    
    @Test
    void testSendWithSubjectAndMessage() throws NotificationException {
        SmsNotifier notifier = SmsNotifier.builder()
                .fromPhoneNumber("+15551234567")
                .addReceiver("+15559876543")
                .build();
        
        NotificationResult result = notifier.send("Alert", "System is down!");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(NotificationChannel.SMS, result.getChannel());
        assertNotNull(result.getProviderId());
        assertTrue(result.getProviderId().startsWith("twilio-"));
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("SMS sent"));
    }
    
    @Test
    void testSendWithOnlySubject() throws NotificationException {
        SmsNotifier notifier = SmsNotifier.builder()
                .fromPhoneNumber("+15551234567")
                .addReceiver("+15559876543")
                .build();
        
        NotificationResult result = notifier.send("Alert", null);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testSendWithOnlyMessage() throws NotificationException {
        SmsNotifier notifier = SmsNotifier.builder()
                .fromPhoneNumber("+15551234567")
                .addReceiver("+15559876543")
                .build();
        
        NotificationResult result = notifier.send(null, "System is down!");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testSendWithEmptySubject() throws NotificationException {
        SmsNotifier notifier = SmsNotifier.builder()
                .fromPhoneNumber("+15551234567")
                .addReceiver("+15559876543")
                .build();
        
        NotificationResult result = notifier.send("", "System is down!");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testSendThrowsExceptionWhenNoReceivers() {
        SmsNotifier notifier = SmsNotifier.builder()
                .fromPhoneNumber("+15551234567")
                .build();
        
        assertThrows(NotificationException.class, () -> {
            notifier.send("Alert", "System is down!");
        });
    }
    
    @Test
    void testSendWithMultipleReceivers() throws NotificationException {
        SmsNotifier notifier = SmsNotifier.builder()
                .fromPhoneNumber("+15551234567")
                .addReceivers("+15559876543", "+15551111111", "+15552222222")
                .build();
        
        NotificationResult result = notifier.send("Alert", "System is down!");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("3 recipients"));
    }
    
    @Test
    void testAddReceiverAfterBuild() throws NotificationException {
        SmsNotifier notifier = SmsNotifier.builder()
                .fromPhoneNumber("+15551234567")
                .addReceiver("+15559876543")
                .build();
        
        notifier.addReceiver("+15551111111");
        
        NotificationResult result = notifier.send("Alert", "System is down!");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("2 recipients"));
    }
    
    @Test
    void testAddReceiversAfterBuild() throws NotificationException {
        SmsNotifier notifier = SmsNotifier.builder()
                .fromPhoneNumber("+15551234567")
                .addReceiver("+15559876543")
                .build();
        
        notifier.addReceivers("+15551111111", "+15552222222");
        
        NotificationResult result = notifier.send("Alert", "System is down!");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("3 recipients"));
    }
    
    @Test
    void testAddNullReceiver() {
        SmsNotifier notifier = SmsNotifier.builder()
                .fromPhoneNumber("+15551234567")
                .addReceiver("+15559876543")
                .build();
        
        // Should not throw exception, just ignore null
        assertDoesNotThrow(() -> notifier.addReceiver(null));
    }
    
    @Test
    void testAddEmptyReceiver() {
        SmsNotifier notifier = SmsNotifier.builder()
                .fromPhoneNumber("+15551234567")
                .addReceiver("+15559876543")
                .build();
        
        // Should not throw exception, just ignore empty string
        assertDoesNotThrow(() -> notifier.addReceiver(""));
    }
    
    @Test
    void testAddReceiverWithWhitespace() throws NotificationException {
        SmsNotifier notifier = SmsNotifier.builder()
                .fromPhoneNumber("+15551234567")
                .addReceiver("  +15559876543  ")
                .build();
        
        NotificationResult result = notifier.send("Alert", "Test");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testChainedAddReceivers() throws NotificationException {
        SmsNotifier notifier = SmsNotifier.builder()
                .fromPhoneNumber("+15551234567")
                .addReceiver("+15559876543")
                .build()
                .addReceiver("+15551111111")
                .addReceiver("+15552222222");
        
        NotificationResult result = notifier.send("Alert", "Test");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("3 recipients"));
    }
    
    @Test
    void testProviderIdFormat() throws NotificationException {
        SmsNotifier notifier = SmsNotifier.builder()
                .providerName("Plivo")
                .fromPhoneNumber("+15551234567")
                .addReceiver("+15559876543")
                .build();
        
        NotificationResult result = notifier.send("Alert", "Test");
        
        assertNotNull(result.getProviderId());
        assertTrue(result.getProviderId().startsWith("plivo-"));
        // UUID format check
        String[] parts = result.getProviderId().split("-", 2);
        assertEquals(2, parts.length);
    }
    
    @Test
    void testResultMessageContainsProviderName() throws NotificationException {
        SmsNotifier notifier = SmsNotifier.builder()
                .providerName("CustomSmsProvider")
                .fromPhoneNumber("+15551234567")
                .addReceiver("+15559876543")
                .build();
        
        NotificationResult result = notifier.send("Alert", "Test");
        
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("CustomSmsProvider"));
    }
    
    @Test
    void testSendMultipleTimes() throws NotificationException {
        SmsNotifier notifier = SmsNotifier.builder()
                .fromPhoneNumber("+15551234567")
                .addReceiver("+15559876543")
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
        assertNotEquals(result1.getProviderId(), result3.getProviderId());
    }
}
