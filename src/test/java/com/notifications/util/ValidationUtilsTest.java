package com.notifications.util;

import com.notifications.core.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilsTest {

    @Test
    void testIsValidEmailWithValidEmails() {
        assertTrue(ValidationUtils.isValidEmail("test@example.com"));
        assertTrue(ValidationUtils.isValidEmail("user.name@domain.co.uk"));
        assertTrue(ValidationUtils.isValidEmail("user+tag@example.com"));
    }

    @Test
    void testIsValidEmailWithInvalidEmails() {
        assertFalse(ValidationUtils.isValidEmail(null));
        assertFalse(ValidationUtils.isValidEmail(""));
        assertFalse(ValidationUtils.isValidEmail("   "));
        assertFalse(ValidationUtils.isValidEmail("notanemail"));
        assertFalse(ValidationUtils.isValidEmail("@example.com"));
        assertFalse(ValidationUtils.isValidEmail("user@"));
    }

    @Test
    void testIsValidPhoneWithValidPhones() {
        assertTrue(ValidationUtils.isValidPhoneNumber("+15551234567"));
        assertTrue(ValidationUtils.isValidPhoneNumber("+442071234567"));
        assertTrue(ValidationUtils.isValidPhoneNumber("+1234567890"));
    }

    @Test
    void testIsValidPhoneWithInvalidPhones() {
        assertFalse(ValidationUtils.isValidPhoneNumber(null));
        assertFalse(ValidationUtils.isValidPhoneNumber(""));
        assertFalse(ValidationUtils.isValidPhoneNumber("   "));
        assertFalse(ValidationUtils.isValidPhoneNumber("0234567890")); // can't start with 0
        assertFalse(ValidationUtils.isValidPhoneNumber("+1")); // too short (needs at least 2 digits after +)
        assertFalse(ValidationUtils.isValidPhoneNumber("abc123")); // contains letters
    }

    @Test
    void testIsNotBlankWithNullAndEmpty() {
        assertFalse(ValidationUtils.isNotBlank(null));
        assertFalse(ValidationUtils.isNotBlank(""));
        assertFalse(ValidationUtils.isNotBlank("   "));
        assertFalse(ValidationUtils.isNotBlank("\t\n"));
    }

    @Test
    void testIsNotBlankWithNonEmpty() {
        assertTrue(ValidationUtils.isNotBlank("text"));
        assertTrue(ValidationUtils.isNotBlank(" text "));
    }
    
    @Test
    void testIsValidUrl() {
        assertTrue(ValidationUtils.isValidUrl("https://example.com"));
        assertTrue(ValidationUtils.isValidUrl("http://api.example.com/webhook"));
        assertFalse(ValidationUtils.isValidUrl(null));
        assertFalse(ValidationUtils.isValidUrl(""));
        assertFalse(ValidationUtils.isValidUrl("not-a-url"));
    }
    
    @Test
    void testIsValidDeviceToken() {
        assertTrue(ValidationUtils.isValidDeviceToken("a".repeat(32)));
        assertTrue(ValidationUtils.isValidDeviceToken("a".repeat(64)));
        assertFalse(ValidationUtils.isValidDeviceToken(null));
        assertFalse(ValidationUtils.isValidDeviceToken(""));
        assertFalse(ValidationUtils.isValidDeviceToken("short"));
    }
    
    @Test
    void testIsValidSlackChannel() {
        assertTrue(ValidationUtils.isValidSlackChannel("#general"));
        assertTrue(ValidationUtils.isValidSlackChannel("https://hooks.slack.com/services/T00/B00/XXX"));
        assertFalse(ValidationUtils.isValidSlackChannel(null));
        assertFalse(ValidationUtils.isValidSlackChannel(""));
        assertFalse(ValidationUtils.isValidSlackChannel("invalid"));
    }
}
