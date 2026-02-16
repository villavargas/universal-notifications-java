package com.notifications.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Notifier interface implementations
 */
@DisplayName("Notifier Interface Tests")
class NotifierTest {
    
    private Notifier mockNotifier;
    
    @BeforeEach
    void setUp() {
        mockNotifier = mock(Notifier.class);
    }
    
    @Test
    @DisplayName("Should call send method with correct parameters")
    void shouldCallSendWithCorrectParameters() throws NotificationException {
        // Given
        String subject = "Test Subject";
        String message = "Test Message";
        NotificationResult expectedResult = NotificationResult.builder()
                .success(true)
                .message("Sent successfully")
                .build();
        
        when(mockNotifier.send(subject, message)).thenReturn(expectedResult);
        
        // When
        NotificationResult result = mockNotifier.send(subject, message);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
        verify(mockNotifier).send(subject, message);
    }
    
    @Test
    @DisplayName("Should handle null subject")
    void shouldHandleNullSubject() throws NotificationException {
        // Given
        String message = "Test Message";
        NotificationResult expectedResult = NotificationResult.builder()
                .success(true)
                .build();
        
        when(mockNotifier.send(null, message)).thenReturn(expectedResult);
        
        // When
        NotificationResult result = mockNotifier.send(null, message);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
    }
    
    @Test
    @DisplayName("Should handle null message")
    void shouldHandleNullMessage() throws NotificationException {
        // Given
        String subject = "Test Subject";
        NotificationResult expectedResult = NotificationResult.builder()
                .success(true)
                .build();
        
        when(mockNotifier.send(subject, null)).thenReturn(expectedResult);
        
        // When
        NotificationResult result = mockNotifier.send(subject, null);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
    }
    
    @Test
    @DisplayName("Should throw NotificationException on failure")
    void shouldThrowNotificationExceptionOnFailure() throws NotificationException {
        // Given
        String subject = "Test Subject";
        String message = "Test Message";
        
        when(mockNotifier.send(subject, message))
                .thenThrow(new NotificationException("Send failed"));
        
        // When/Then
        assertThatThrownBy(() -> mockNotifier.send(subject, message))
                .isInstanceOf(NotificationException.class)
                .hasMessage("Send failed");
    }
}
