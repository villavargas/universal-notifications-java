package com.notifications.service.email;

import com.notifications.config.ProviderConfig;
import com.notifications.core.NotificationChannel;
import com.notifications.core.NotificationException;
import com.notifications.core.NotificationResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EmailNotifier (SMTP generic)
 */
class EmailNotifierGenericTest {
    
    @Test
    void testBuilderWithMinimalConfiguration() {
        EmailNotifier notifier = EmailNotifier.builder()
                .smtpHost("smtp.example.com")
                .from("sender@example.com")
                .addReceiver("recipient@example.com")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithFullConfiguration() {
        EmailNotifier notifier = EmailNotifier.builder()
                .smtpHost("smtp.gmail.com")
                .smtpPort(587)
                .username("user@gmail.com")
                .password("app-password")
                .from("noreply@example.com")
                .fromName("My Application")
                .addReceiver("user1@example.com")
                .addReceiver("user2@example.com")
                .usePlainText(false)
                .useSSL(true)
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithCustomPort() {
        EmailNotifier notifier = EmailNotifier.builder()
                .smtpHost("smtp.example.com")
                .smtpPort(465)
                .from("sender@example.com")
                .addReceiver("recipient@example.com")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithSSL() {
        EmailNotifier notifier = EmailNotifier.builder()
                .smtpHost("smtp.example.com")
                .from("sender@example.com")
                .addReceiver("recipient@example.com")
                .useSSL(true)
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithPlainText() {
        EmailNotifier notifier = EmailNotifier.builder()
                .smtpHost("smtp.example.com")
                .from("sender@example.com")
                .addReceiver("recipient@example.com")
                .usePlainText(true)
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithHTMLEmail() {
        EmailNotifier notifier = EmailNotifier.builder()
                .smtpHost("smtp.example.com")
                .from("sender@example.com")
                .addReceiver("recipient@example.com")
                .usePlainText(false)
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithAuthentication() {
        EmailNotifier notifier = EmailNotifier.builder()
                .smtpHost("smtp.example.com")
                .username("user@example.com")
                .password("password123")
                .from("sender@example.com")
                .addReceiver("recipient@example.com")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithDefaultSmtpHost() {
        // Builder should use default localhost if smtpHost is not specified
        EmailNotifier notifier = EmailNotifier.builder()
                .from("sender@example.com")
                .addReceiver("recipient@example.com")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderThrowsExceptionWhenFromMissing() {
        assertThrows(IllegalArgumentException.class, () -> {
            EmailNotifier.builder()
                    .smtpHost("smtp.example.com")
                    .addReceiver("recipient@example.com")
                    .build();
        });
    }
    
    @Test
    void testBuilderAllowsNoReceiversButSendFails() {
        EmailNotifier notifier = EmailNotifier.builder()
                .smtpHost("smtp.example.com")
                .from("sender@example.com")
                .build();
        
        assertNotNull(notifier);
        
        assertThrows(NotificationException.class, () -> {
            notifier.send("Subject", "Message");
        });
    }
    
    @Test
    void testSendWithSubjectAndMessage() throws NotificationException {
        EmailNotifier notifier = EmailNotifier.builder()
                .smtpHost("smtp.example.com")
                .from("sender@example.com")
                .addReceiver("recipient@example.com")
                .build();
        
        NotificationResult result = notifier.send("Test Subject", "Test Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getProviderId());
        assertTrue(result.getProviderId().startsWith("smtp-"));
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("SMTP"));
    }
    
    @Test
    void testSendWithNullSubject() throws NotificationException {
        EmailNotifier notifier = EmailNotifier.builder()
                .smtpHost("smtp.example.com")
                .from("sender@example.com")
                .addReceiver("recipient@example.com")
                .build();
        
        NotificationResult result = notifier.send(null, "Test Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testSendWithNullMessage() throws NotificationException {
        EmailNotifier notifier = EmailNotifier.builder()
                .smtpHost("smtp.example.com")
                .from("sender@example.com")
                .addReceiver("recipient@example.com")
                .build();
        
        NotificationResult result = notifier.send("Test Subject", null);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testSendToMultipleRecipients() throws NotificationException {
        EmailNotifier notifier = EmailNotifier.builder()
                .smtpHost("smtp.example.com")
                .from("sender@example.com")
                .addReceiver("user1@example.com")
                .addReceiver("user2@example.com")
                .addReceiver("user3@example.com")
                .build();
        
        NotificationResult result = notifier.send("Subject", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("3 recipients"));
    }
    
    @Test
    void testSendMultipleTimes() throws NotificationException {
        EmailNotifier notifier = EmailNotifier.builder()
                .smtpHost("smtp.example.com")
                .from("sender@example.com")
                .addReceiver("recipient@example.com")
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
        
        assertNotEquals(result1.getProviderId(), result2.getProviderId());
        assertNotEquals(result2.getProviderId(), result3.getProviderId());
    }
    
    @Test
    void testAddReceiverAfterBuild() throws NotificationException {
        EmailNotifier notifier = EmailNotifier.builder()
                .smtpHost("smtp.example.com")
                .from("sender@example.com")
                .addReceiver("user1@example.com")
                .build();
        
        notifier.addReceiver("user2@example.com");
        
        NotificationResult result = notifier.send("Subject", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("2 recipients"));
    }
    
    @Test
    void testAddReceiversAfterBuild() throws NotificationException {
        EmailNotifier notifier = EmailNotifier.builder()
                .smtpHost("smtp.example.com")
                .from("sender@example.com")
                .addReceiver("user1@example.com")
                .build();
        
        notifier.addReceivers("user2@example.com", "user3@example.com");
        
        NotificationResult result = notifier.send("Subject", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("3 recipients"));
    }
    
    @Test
    void testAddNullReceiver() {
        EmailNotifier notifier = EmailNotifier.builder()
                .smtpHost("smtp.example.com")
                .from("sender@example.com")
                .addReceiver("user@example.com")
                .build();
        
        assertDoesNotThrow(() -> notifier.addReceiver(null));
    }
    
    @Test
    void testAddEmptyReceiver() {
        EmailNotifier notifier = EmailNotifier.builder()
                .smtpHost("smtp.example.com")
                .from("sender@example.com")
                .addReceiver("user@example.com")
                .build();
        
        assertDoesNotThrow(() -> notifier.addReceiver(""));
    }
    
    @Test
    void testAddReceiverWithWhitespace() throws NotificationException {
        EmailNotifier notifier = EmailNotifier.builder()
                .smtpHost("smtp.example.com")
                .from("sender@example.com")
                .addReceiver("  user@example.com  ")
                .build();
        
        NotificationResult result = notifier.send("Subject", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testDefaultSmtpPort() {
        EmailNotifier notifier = EmailNotifier.builder()
                .smtpHost("smtp.example.com")
                .from("sender@example.com")
                .addReceiver("user@example.com")
                .build();
        
        assertNotNull(notifier);
        // Default port should be 587
    }
    
    @Test
    void testFromConfigWithMinimal() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SMTP")
                .apiKey("dummy")
                .from("sender@example.com")
                .property("smtpHost", "smtp.example.com")
                .property("receivers", "user@example.com")
                .build();
        
        EmailNotifier notifier = EmailNotifier.fromConfig(config);
        
        assertNotNull(notifier);
    }
    
    @Test
    void testFromConfigWithAllOptions() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SMTP")
                .apiKey("dummy")
                .from("sender@example.com")
                .property("smtpHost", "smtp.gmail.com")
                .property("smtpPort", "587")
                .property("username", "user@gmail.com")
                .property("password", "password123")
                .property("fromName", "My App")
                .property("receivers", "user1@example.com,user2@example.com")
                .property("usePlainText", "false")
                .property("useSSL", "true")
                .build();
        
        EmailNotifier notifier = EmailNotifier.fromConfig(config);
        
        assertNotNull(notifier);
    }
    
    @Test
    void testFromConfigWithDefaults() {
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SMTP")
                .apiKey("dummy")
                .from("sender@example.com")
                .property("smtpHost", "smtp.example.com")
                .build();
        
        EmailNotifier notifier = EmailNotifier.fromConfig(config);
        
        assertNotNull(notifier);
    }
    
    @Test
    void testChainedAddReceivers() throws NotificationException {
        EmailNotifier notifier = EmailNotifier.builder()
                .smtpHost("smtp.example.com")
                .from("sender@example.com")
                .addReceiver("user1@example.com")
                .build()
                .addReceiver("user2@example.com")
                .addReceiver("user3@example.com");
        
        NotificationResult result = notifier.send("Subject", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("3 recipients"));
    }
    
    @Test
    void testProviderIdFormat() throws NotificationException {
        EmailNotifier notifier = EmailNotifier.builder()
                .smtpHost("smtp.example.com")
                .from("sender@example.com")
                .addReceiver("user@example.com")
                .build();
        
        NotificationResult result = notifier.send("Subject", "Message");
        
        assertNotNull(result.getProviderId());
        assertTrue(result.getProviderId().startsWith("smtp-"));
        String[] parts = result.getProviderId().split("-", 2);
        assertEquals(2, parts.length);
    }
}
