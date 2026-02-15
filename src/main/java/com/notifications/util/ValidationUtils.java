package com.notifications.util;

import java.util.regex.Pattern;

/**
 * Utility class for validating notification data
 */
public final class ValidationUtils {
    
    // Email validation regex (RFC 5322 simplified)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    
    // Phone number validation (international format)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?[1-9]\\d{1,14}$"
    );
    
    // URL validation (for webhooks, etc.)
    private static final Pattern URL_PATTERN = Pattern.compile(
        "^https?://[^\\s/$.?#].[^\\s]*$",
        Pattern.CASE_INSENSITIVE
    );
    
    private ValidationUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Validates an email address
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validates a phone number (E.164 format)
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }
        // Remove common separators
        String cleaned = phoneNumber.replaceAll("[\\s\\-\\(\\)]", "");
        return PHONE_PATTERN.matcher(cleaned).matches();
    }
    
    /**
     * Validates a URL
     */
    public static boolean isValidUrl(String url) {
        return url != null && URL_PATTERN.matcher(url).matches();
    }
    
    /**
     * Checks if a string is not null and not blank
     */
    public static boolean isNotBlank(String str) {
        return str != null && !str.isBlank();
    }
    
    /**
     * Validates a device token (for push notifications)
     * Basic check - actual format depends on the provider (Firebase, APNS, etc.)
     */
    public static boolean isValidDeviceToken(String token) {
        return isNotBlank(token) && token.length() >= 32;
    }
    
    /**
     * Validates a Slack channel name
     */
    public static boolean isValidSlackChannel(String channel) {
        if (!isNotBlank(channel)) {
            return false;
        }
        // Slack channels start with # or are webhooks
        return channel.startsWith("#") || isValidUrl(channel);
    }
}
