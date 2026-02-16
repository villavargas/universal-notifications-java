package com.notifications.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Notify class
 */
@DisplayName("Notify Class Tests")
class NotifyTest {
    
    private Notifier mockNotifier1;
    private Notifier mockNotifier2;
    private NotificationResult successResult;
    
    @BeforeEach
    void setUp() {
        mockNotifier1 = mock(Notifier.class);
        mockNotifier2 = mock(Notifier.class);
        
        successResult = NotificationResult.builder()
                .success(true)
                .providerId("test-provider-1")
                .message("Sent successfully")
                .build();
    }
    
    @Test
    @DisplayName("Should create Notify instance")
    void shouldCreateNotifyInstance() {
        // When
        Notify notify = Notify.create();
        
        // Then
        assertThat(notify).isNotNull();
        assertThat(notify.getNotifierCount()).isZero();
        assertThat(notify.isDisabled()).isFalse();
    }
    
    @Test
    @DisplayName("Should create disabled Notify instance")
    void shouldCreateDisabledNotifyInstance() {
        // When
        Notify notify = Notify.createDisabled();
        
        // Then
        assertThat(notify).isNotNull();
        assertThat(notify.isDisabled()).isTrue();
    }
    
    @Test
    @DisplayName("Should add notifier using use() method")
    void shouldAddNotifierUsingUse() {
        // Given
        Notify notify = Notify.create();
        
        // When
        notify.use(mockNotifier1);
        
        // Then
        assertThat(notify.getNotifierCount()).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Should add multiple notifiers using use() varargs")
    void shouldAddMultipleNotifiersUsingVarargs() {
        // Given
        Notify notify = Notify.create();
        
        // When
        notify.use(mockNotifier1, mockNotifier2);
        
        // Then
        assertThat(notify.getNotifierCount()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("Should ignore null notifiers")
    void shouldIgnoreNullNotifiers() {
        // Given
        Notify notify = Notify.create();
        
        // When
        notify.use(mockNotifier1, null, mockNotifier2);
        
        // Then
        assertThat(notify.getNotifierCount()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("Should send to single notifier")
    void shouldSendToSingleNotifier() throws NotificationException {
        // Given
        Notify notify = Notify.create().use(mockNotifier1);
        when(mockNotifier1.send("Subject", "Message")).thenReturn(successResult);
        
        // When
        NotificationResult result = notify.send("Subject", "Message");
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        verify(mockNotifier1).send("Subject", "Message");
    }
    
    @Test
    @DisplayName("Should send to multiple notifiers")
    void shouldSendToMultipleNotifiers() throws NotificationException {
        // Given
        Notify notify = Notify.create()
                .use(mockNotifier1)
                .use(mockNotifier2);
        
        NotificationResult result1 = NotificationResult.builder()
                .success(true)
                .providerId("provider-1")
                .build();
        
        NotificationResult result2 = NotificationResult.builder()
                .success(true)
                .providerId("provider-2")
                .build();
        
        when(mockNotifier1.send("Subject", "Message")).thenReturn(result1);
        when(mockNotifier2.send("Subject", "Message")).thenReturn(result2);
        
        // When
        NotificationResult result = notify.send("Subject", "Message");
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.isComposite()).isTrue();
        assertThat(result.getIndividualResults()).hasSize(2);
        
        verify(mockNotifier1).send("Subject", "Message");
        verify(mockNotifier2).send("Subject", "Message");
    }
    
    @Test
    @DisplayName("Should return disabled result when disabled")
    void shouldReturnDisabledResultWhenDisabled() throws NotificationException {
        // Given
        Notify notify = Notify.createDisabled().use(mockNotifier1);
        
        // When
        NotificationResult result = notify.send("Subject", "Message");
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getMessage()).contains("disabled");
        verify(mockNotifier1, never()).send(anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should return no-notifiers result when no notifiers configured")
    void shouldReturnNoNotifiersResult() throws NotificationException {
        // Given
        Notify notify = Notify.create();
        
        // When
        NotificationResult result = notify.send("Subject", "Message");
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getMessage()).contains("No notifiers");
    }
    
    @Test
    @DisplayName("Should enable and disable notify")
    void shouldEnableAndDisableNotify() throws NotificationException {
        // Given
        Notify notify = Notify.create().use(mockNotifier1);
        when(mockNotifier1.send(anyString(), anyString())).thenReturn(successResult);
        
        // When enabled
        NotificationResult result1 = notify.send("Subject", "Message");
        
        // Then
        assertThat(result1.isSuccess()).isTrue();
        verify(mockNotifier1, times(1)).send(anyString(), anyString());
        
        // When disabled
        notify.disable();
        NotificationResult result2 = notify.send("Subject", "Message");
        
        // Then
        assertThat(result2.getMessage()).contains("disabled");
        verify(mockNotifier1, times(1)).send(anyString(), anyString()); // Still only 1 call
        
        // When re-enabled
        notify.enable();
        NotificationResult result3 = notify.send("Subject", "Message");
        
        // Then
        assertThat(result3.isSuccess()).isTrue();
        verify(mockNotifier1, times(2)).send(anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should send asynchronously")
    void shouldSendAsynchronously() throws Exception {
        // Given
        Notify notify = Notify.create().use(mockNotifier1);
        when(mockNotifier1.send("Subject", "Message")).thenReturn(successResult);
        
        // When
        CompletableFuture<NotificationResult> future = notify.sendAsync("Subject", "Message");
        NotificationResult result = future.get(5, TimeUnit.SECONDS);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        verify(mockNotifier1).send("Subject", "Message");
    }
    
    @Test
    @DisplayName("Should handle partial failures in composite send")
    void shouldHandlePartialFailures() throws NotificationException {
        // Given
        Notify notify = Notify.create()
                .use(mockNotifier1)
                .use(mockNotifier2);
        
        NotificationResult successResult = NotificationResult.builder()
                .success(true)
                .providerId("provider-1")
                .build();
        
        when(mockNotifier1.send("Subject", "Message")).thenReturn(successResult);
        when(mockNotifier2.send("Subject", "Message"))
                .thenThrow(new NotificationException("Failed"));
        
        // When
        NotificationResult result = notify.send("Subject", "Message");
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.isComposite()).isTrue();
        assertThat(result.getIndividualResults()).hasSize(1); // Only successful ones
        assertThat(result.getMessage()).contains("1/2");
    }
    
    @Test
    @DisplayName("Should throw exception when all notifiers fail")
    void shouldThrowExceptionWhenAllFail() throws NotificationException {
        // Given
        Notify notify = Notify.create()
                .use(mockNotifier1)
                .use(mockNotifier2);
        
        when(mockNotifier1.send("Subject", "Message"))
                .thenThrow(new NotificationException("Failed 1"));
        when(mockNotifier2.send("Subject", "Message"))
                .thenThrow(new NotificationException("Failed 2"));
        
        // When/Then
        assertThatThrownBy(() -> notify.send("Subject", "Message"))
                .isInstanceOf(NotificationException.class);
    }
    
    @Test
    @DisplayName("Should chain use() calls fluently")
    void shouldChainUseCallsFluently() {
        // When
        Notify notify = Notify.create()
                .use(mockNotifier1)
                .use(mockNotifier2);
        
        // Then
        assertThat(notify.getNotifierCount()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("Should create with notifiers using withNotifiers()")
    void shouldCreateWithNotifiersUsingFactory() {
        // When
        Notify notify = Notify.withNotifiers(mockNotifier1, mockNotifier2);
        
        // Then
        assertThat(notify).isNotNull();
        assertThat(notify.getNotifierCount()).isEqualTo(2);
    }
}
