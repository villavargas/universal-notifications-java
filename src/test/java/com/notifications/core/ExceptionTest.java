package com.notifications.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for core exception classes.
 */
class ExceptionTest {

    @Test
    void testValidationException() {
        ValidationException exception = new ValidationException("Validation failed");
        assertEquals("Validation failed", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testValidationExceptionWithCause() {
        Throwable cause = new IllegalArgumentException("Invalid argument");
        ValidationException exception = new ValidationException("Validation failed", cause);
        assertEquals("Validation failed", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testProviderException() {
        ProviderException exception = new ProviderException("Provider error");
        assertEquals("Provider error", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testProviderExceptionWithCause() {
        Throwable cause = new RuntimeException("Connection timeout");
        ProviderException exception = new ProviderException("Provider error", cause);
        assertEquals("Provider error", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testNotificationExceptionWithErrorType() {
        NotificationException exception = new NotificationException(
                "Validation error", 
                NotificationException.ErrorType.VALIDATION_ERROR
        );
        assertEquals("Validation error", exception.getMessage());
        assertEquals(NotificationException.ErrorType.VALIDATION_ERROR, exception.getErrorType());
        assertNull(exception.getCause());
    }

    @Test
    void testNotificationExceptionWithCauseAndErrorType() {
        Throwable cause = new Exception("Root cause");
        NotificationException exception = new NotificationException(
                "Provider error",
                cause,
                NotificationException.ErrorType.PROVIDER_ERROR
        );
        assertEquals("Provider error", exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertEquals(NotificationException.ErrorType.PROVIDER_ERROR, exception.getErrorType());
    }

    @Test
    void testErrorTypeValues() {
        NotificationException.ErrorType[] types = NotificationException.ErrorType.values();
        assertEquals(6, types.length);
        
        // Verify specific error types exist
        assertNotNull(NotificationException.ErrorType.valueOf("VALIDATION_ERROR"));
        assertNotNull(NotificationException.ErrorType.valueOf("PROVIDER_ERROR"));
        assertNotNull(NotificationException.ErrorType.valueOf("CONFIGURATION_ERROR"));
        assertNotNull(NotificationException.ErrorType.valueOf("NETWORK_ERROR"));
        assertNotNull(NotificationException.ErrorType.valueOf("RATE_LIMIT_ERROR"));
        assertNotNull(NotificationException.ErrorType.valueOf("UNKNOWN_ERROR"));
    }
}
