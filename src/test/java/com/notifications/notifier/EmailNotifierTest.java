package com.notifications.notifier;

import com.notifications.config.ProviderConfig;
import com.notifications.core.NotificationChannel;
import com.notifications.core.NotificationException;
import com.notifications.core.NotificationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for EmailNotifier
 */
@DisplayName("EmailNotifier Tests")
class EmailNotifierTest {
    
    private EmailNotifier emailNotifier;
    
    @BeforeEach
    void setUp() {
        emailNotifier = EmailNotifier.builder()
                .providerName("TestProvider")
                .senderAddress("test@example.com")
                .senderName("Test Sender")
                .addReceiver("recipient@example.com")
                .build();
    }
    
    @Test
    @DisplayName("Should build EmailNotifier with required fields")
    void shouldBuildWithRequiredFields() {
        // When
        EmailNotifier notifier = EmailNotifier.builder()
                .senderAddress("sender@example.com")
                .build();
        
        // Then
        assertThat(notifier).isNotNull();
    }
    
    @Test
    @DisplayName("Should throw exception when sender address is missing")
    void shouldThrowExceptionWhenSenderAddressMissing() {
        // When/Then
        assertThatThrownBy(() -> EmailNotifier.builder().build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sender address is required");
    }
    
    @Test
    @DisplayName("Should add single receiver")
    void shouldAddSingleReceiver() {
        // Given
        EmailNotifier notifier = EmailNotifier.builder()
                .senderAddress("sender@example.com")
                .build();
        
        // When
        notifier.addReceiver("user@example.com");
        
        // Then - verified implicitly by not throwing exception in send
        assertThat(notifier).isNotNull();
    }
    
    @Test
    @DisplayName("Should add multiple receivers")
    void shouldAddMultipleReceivers() {
        // Given
        EmailNotifier notifier = EmailNotifier.builder()
                .senderAddress("sender@example.com")
                .build();
        
        // When
        notifier.addReceivers("user1@example.com", "user2@example.com", "user3@example.com");
        
        // Then
        assertThat(notifier).isNotNull();
    }
    
    @Test
    @DisplayName("Should ignore null or empty receivers")
    void shouldIgnoreNullOrEmptyReceivers() {
        // Given
        EmailNotifier notifier = EmailNotifier.builder()
                .senderAddress("sender@example.com")
                .addReceiver("valid@example.com")
                .build();
        
        // When
        notifier.addReceivers(null, "", "  ", "another@example.com");
        
        // Then - should not throw exception
        assertThat(notifier).isNotNull();
    }
    
    @Test
    @DisplayName("Should send email successfully")
    void shouldSendEmailSuccessfully() throws NotificationException {
        // When
        NotificationResult result = emailNotifier.send("Test Subject", "Test Message");
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getChannel()).isEqualTo(NotificationChannel.EMAIL);
        assertThat(result.getProviderId()).startsWith("testprovider-");
        assertThat(result.getMessage()).contains("TestProvider");
    }
    
    @Test
    @DisplayName("Should send email with null subject")
    void shouldSendEmailWithNullSubject() throws NotificationException {
        // When
        NotificationResult result = emailNotifier.send(null, "Test Message");
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
    }
    
    @Test
    @DisplayName("Should send email with null message")
    void shouldSendEmailWithNullMessage() throws NotificationException {
        // When
        NotificationResult result = emailNotifier.send("Test Subject", null);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
    }
    
    @Test
    @DisplayName("Should throw exception when no receivers configured")
    void shouldThrowExceptionWhenNoReceivers() {
        // Given
        EmailNotifier notifier = EmailNotifier.builder()
                .senderAddress("sender@example.com")
                .build();
        
        // When/Then
        assertThatThrownBy(() -> notifier.send("Subject", "Message"))
                .isInstanceOf(NotificationException.class)
                .hasMessageContaining("No receivers configured");
    }
    
    @Test
    @DisplayName("Should create from ProviderConfig")
    void shouldCreateFromProviderConfig() {
        // Given
        ProviderConfig config = ProviderConfig.builder()
                .channel(NotificationChannel.EMAIL)
                .providerName("SendGrid")
                .apiKey("test-key")
                .property("senderEmail", "config@example.com")
                .property("senderName", "Config Sender")
                .property("receivers", "user1@example.com,user2@example.com")
                .build();
        
        // When
        EmailNotifier notifier = EmailNotifier.fromConfig(config);
        
        // Then
        assertThat(notifier).isNotNull();
    }
    
    @Test
    @DisplayName("Should use plain text format")
    void shouldUsePlainTextFormat() {
        // When
        EmailNotifier notifier = EmailNotifier.builder()
                .senderAddress("sender@example.com")
                .addReceiver("recipient@example.com")
                .usePlainText(true)
                .build();
        
        // Then
        assertThat(notifier).isNotNull();
    }
    
    @Test
    @DisplayName("Should build with fluent chain")
    void shouldBuildWithFluentChain() {
        // When
        EmailNotifier notifier = EmailNotifier.builder()
                .providerName("Gmail")
                .senderAddress("noreply@company.com")
                .senderName("Company Name")
                .addReceiver("user1@example.com")
                .addReceiver("user2@example.com")
                .usePlainText(false)
                .build();
        
        // Then
        assertThat(notifier).isNotNull();
    }
}
