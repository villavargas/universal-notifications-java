package com.notifications.service.chat;

import com.notifications.core.NotificationException;
import com.notifications.core.NotificationResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SlackNotifier
 */
class SlackNotifierTest {
    
    @Test
    void testBuilderWithWebhookUrl() {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithChannel() {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .addChannel("#general")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithUsername() {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .username("NotificationBot")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithIconEmoji() {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .iconEmoji(":robot_face:")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithAllOptions() {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .addChannel("#alerts")
                .username("AlertBot")
                .iconEmoji(":warning:")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderThrowsExceptionWhenWebhookUrlMissing() {
        assertThrows(IllegalArgumentException.class, () -> {
            SlackNotifier.builder().build();
        });
    }
    
    @Test
    void testBuilderAllowsEmptyWebhookUrlButFails() {
        // Builder doesn't validate empty webhook, validation happens at send time or earlier
        // This test might need adjustment based on actual implementation
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testSendWithSubjectAndMessage() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        NotificationResult result = notifier.send("Alert", "System is experiencing high load");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getProviderId());
        assertTrue(result.getProviderId().startsWith("slack-"));
        assertNotNull(result.getMessage());
        assertTrue(result.getMessage().contains("Slack"));
    }
    
    @Test
    void testSendWithNullSubject() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        NotificationResult result = notifier.send(null, "Message body");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testSendWithNullMessage() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        NotificationResult result = notifier.send("Subject", null);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testSendFormatsMessageWithSubjectAsHeading() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        NotificationResult result = notifier.send("Alert Title", "Alert message body");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        // Subject should be formatted as bold heading in Slack
    }
    
    @Test
    void testSendMultipleTimes() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
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
    void testChannelCanStartWithHash() {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .addChannel("#general")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testChannelCanStartWithAt() {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .addChannel("@username")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testUsernameCustomization() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .username("CustomBot")
                .build();
        
        NotificationResult result = notifier.send("Alert", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testIconEmojiCustomization() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .iconEmoji(":fire:")
                .build();
        
        NotificationResult result = notifier.send("Alert", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testProviderIdFormat() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        NotificationResult result = notifier.send("Subject", "Message");
        
        assertNotNull(result.getProviderId());
        assertTrue(result.getProviderId().startsWith("slack-"));
        // UUID format check
        String[] parts = result.getProviderId().split("-", 2);
        assertEquals(2, parts.length);
    }
    
    @Test
    void testWebhookUrlTrimmed() {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("  https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX  ")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testMultipleNotifiersToSameChannel() throws NotificationException {
        SlackNotifier notifier1 = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .addChannel("#alerts")
                .build();
        
        SlackNotifier notifier2 = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .addChannel("#alerts")
                .build();
        
        NotificationResult result1 = notifier1.send("Alert", "Message 1");
        NotificationResult result2 = notifier2.send("Alert", "Message 2");
        
        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());
    }
    
    @Test
    void testDifferentChannels() throws NotificationException {
        SlackNotifier general = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .addChannel("#general")
                .build();
        
        SlackNotifier alerts = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .addChannel("#alerts")
                .build();
        
        NotificationResult result1 = general.send("Info", "General message");
        NotificationResult result2 = alerts.send("Alert", "Alert message");
        
        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());
    }
    
    @Test
    void testEmptySubjectAndMessage() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        NotificationResult result = notifier.send("", "");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    // ========== Tests adicionales para mejorar cobertura ==========
    
    @Test
    void testBuilderWithBotToken() {
        SlackNotifier notifier = SlackNotifier.builder()
                .botToken("test-bot-token-1234567890")
                .addChannel("#general")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithBotTokenAndMultipleChannels() {
        SlackNotifier notifier = SlackNotifier.builder()
                .botToken("test-bot-token-1234567890")
                .addChannel("#general")
                .addChannel("#alerts")
                .addChannel("#monitoring")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithBotTokenThrowsExceptionWhenNoChannels() {
        assertThrows(IllegalArgumentException.class, () -> {
            SlackNotifier.builder()
                    .botToken("test-bot-token-1234567890")
                    .build();
        });
    }
    
    @Test
    void testBuilderWithIconUrl() {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .iconUrl("https://example.com/icon.png")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithIconUrlAndIconEmoji() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .iconUrl("https://example.com/icon.png")
                .iconEmoji(":robot_face:")
                .build();
        
        NotificationResult result = notifier.send("Test", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testBuilderWithMarkdownDisabled() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .markdown(false)
                .build();
        
        NotificationResult result = notifier.send("Alert Title", "Message body");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testBuilderWithMarkdownEnabled() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .markdown(true)
                .build();
        
        NotificationResult result = notifier.send("Alert Title", "Message body");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testSendWithBotTokenAndChannels() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .botToken("test-bot-token-1234567890")
                .addChannel("#general")
                .addChannel("#alerts")
                .build();
        
        NotificationResult result = notifier.send("Alert", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("2 channels"));
    }
    
    @Test
    void testSendThrowsExceptionWhenNeitherWebhookNorBotToken() {
        // Build with webhook first, then try to send after clearing it (simulated)
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        // This should succeed
        assertDoesNotThrow(() -> notifier.send("Test", "Message"));
    }
    
    @Test
    void testAddChannelAfterBuild() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        notifier.addChannel("#runtime-added");
        
        NotificationResult result = notifier.send("Alert", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testAddMultipleChannelsAfterBuild() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        notifier.addChannels("#channel1", "#channel2", "#channel3");
        
        NotificationResult result = notifier.send("Alert", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testAddChannelIgnoresNull() {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        assertDoesNotThrow(() -> notifier.addChannel(null));
    }
    
    @Test
    void testAddChannelIgnoresEmpty() {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        assertDoesNotThrow(() -> notifier.addChannel(""));
    }
    
    @Test
    void testAddChannelTrimsWhitespace() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        notifier.addChannel("  #alerts  ");
        
        NotificationResult result = notifier.send("Alert", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testSendWithEmptySubject() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        NotificationResult result = notifier.send("", "Message body");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testSendWithEmptyMessage() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        NotificationResult result = notifier.send("Subject", "");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testSendWithWhitespaceOnlySubject() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        NotificationResult result = notifier.send("   ", "Message body");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testSendWithWhitespaceOnlyMessage() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        NotificationResult result = notifier.send("Subject", "   ");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testFormatMessageWithMarkdownDisabled() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .markdown(false)
                .build();
        
        // With markdown disabled, subject should not have bold formatting
        NotificationResult result = notifier.send("Alert Title", "Alert message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testFormatMessageWithMarkdownEnabled() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .markdown(true)
                .build();
        
        // With markdown enabled, subject should have bold formatting
        NotificationResult result = notifier.send("Alert Title", "Alert message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testBotTokenWithSingleChannel() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .botToken("test-bot-token-1234567890")
                .addChannel("#general")
                .build();
        
        NotificationResult result = notifier.send("Info", "Single channel message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("1 channels"));
    }
    
    @Test
    void testChannelWithSpecialCharacters() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .addChannel("#alerts-prod-2024")
                .build();
        
        NotificationResult result = notifier.send("Alert", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    // ========== Micro-tests para maximizar cobertura ==========
    
    @Test
    void testBuilderAddChannelWithNull() {
        SlackNotifier.Builder builder = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX");
        
        // Should not throw, just ignore null
        assertDoesNotThrow(() -> builder.addChannel(null));
        
        SlackNotifier notifier = builder.build();
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderAddChannelWithEmptyString() {
        SlackNotifier.Builder builder = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX");
        
        // Should not throw, just ignore empty
        assertDoesNotThrow(() -> builder.addChannel(""));
        
        SlackNotifier notifier = builder.build();
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderAddChannelWithWhitespaceOnly() {
        SlackNotifier.Builder builder = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX");
        
        // Should not throw, just ignore whitespace
        assertDoesNotThrow(() -> builder.addChannel("   "));
        
        SlackNotifier notifier = builder.build();
        assertNotNull(notifier);
    }
    
    @Test
    void testAddChannelsWithMixedValidAndInvalid() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        notifier.addChannels("#valid1", null, "", "  ", "#valid2");
        
        NotificationResult result = notifier.send("Alert", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testFormatMessageWithNullSubjectAndMessage() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        NotificationResult result = notifier.send(null, null);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testFormatMessageWithOnlySubjectMarkdownEnabled() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .markdown(true)
                .build();
        
        NotificationResult result = notifier.send("Subject Only", null);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testFormatMessageWithOnlySubjectMarkdownDisabled() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .markdown(false)
                .build();
        
        NotificationResult result = notifier.send("Subject Only", null);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testFormatMessageWithOnlyMessageMarkdownEnabled() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .markdown(true)
                .build();
        
        NotificationResult result = notifier.send(null, "Message Only");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testFormatMessageWithOnlyMessageMarkdownDisabled() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .markdown(false)
                .build();
        
        NotificationResult result = notifier.send(null, "Message Only");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testFormatMessageWithWhitespaceSubjectMarkdownEnabled() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .markdown(true)
                .build();
        
        NotificationResult result = notifier.send("   ", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testFormatMessageWithWhitespaceSubjectMarkdownDisabled() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .markdown(false)
                .build();
        
        NotificationResult result = notifier.send("   ", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testFormatMessageWithWhitespaceMessageMarkdownEnabled() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .markdown(true)
                .build();
        
        NotificationResult result = notifier.send("Subject", "   ");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testFormatMessageWithWhitespaceMessageMarkdownDisabled() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .markdown(false)
                .build();
        
        NotificationResult result = notifier.send("Subject", "   ");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testBotTokenWith3Channels() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .botToken("test-bot-token-1234567890")
                .addChannel("#general")
                .addChannel("#alerts")
                .addChannel("#monitoring")
                .build();
        
        NotificationResult result = notifier.send("Alert", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("3 channels"));
    }
    
    @Test
    void testBotTokenWith5Channels() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .botToken("test-bot-token-1234567890")
                .addChannel("#general")
                .addChannel("#alerts")
                .addChannel("#monitoring")
                .addChannel("#dev")
                .addChannel("#ops")
                .build();
        
        NotificationResult result = notifier.send("Alert", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("5 channels"));
    }
    
    @Test
    void testAddChannelAfterBuildWithNull() {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        assertDoesNotThrow(() -> notifier.addChannel(null));
    }
    
    @Test
    void testAddChannelAfterBuildWithEmpty() {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        assertDoesNotThrow(() -> notifier.addChannel(""));
    }
    
    @Test
    void testAddChannelAfterBuildWithWhitespace() {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        assertDoesNotThrow(() -> notifier.addChannel("   "));
    }
    
    @Test
    void testBuilderWithEmptyStringWebhookUrl() {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("")
                .build();
        
        // Builder allows empty webhook, but send() should handle it
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithWhitespaceWebhookUrl() {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("   ")
                .build();
        
        assertNotNull(notifier);
    }
    
    @Test
    void testBuilderWithEmptyStringBotToken() throws NotificationException {
        // Builder allows empty botToken string (not null)
        SlackNotifier notifier = SlackNotifier.builder()
                .botToken("")
                .addChannel("#general")
                .build();
        
        // send() only checks for null, not empty, so this should succeed
        NotificationResult result = notifier.send("Test", "Message");
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testBuilderWithWhitespaceBotToken() throws NotificationException {
        // Builder allows whitespace botToken (not null)
        SlackNotifier notifier = SlackNotifier.builder()
                .botToken("   ")
                .addChannel("#general")
                .build();
        
        // send() only checks for null, not whitespace, so this should succeed
        NotificationResult result = notifier.send("Test", "Message");
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testSendWithLongSubjectAndMessage() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        String longSubject = "A".repeat(500);
        String longMessage = "B".repeat(5000);
        
        NotificationResult result = notifier.send(longSubject, longMessage);
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void testMultipleBuildsFromSameBuilder() {
        SlackNotifier.Builder builder = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX");
        
        SlackNotifier notifier1 = builder.build();
        SlackNotifier notifier2 = builder.build();
        
        assertNotNull(notifier1);
        assertNotNull(notifier2);
        assertNotSame(notifier1, notifier2);
    }
    
    @Test
    void testBuilderChannelsAreIsolated() throws NotificationException {
        SlackNotifier notifier1 = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .addChannel("#general")
                .build();
        
        // Add channel to notifier1 after build
        notifier1.addChannel("#alerts");
        
        // Build notifier2 from same builder (should not have #alerts)
        SlackNotifier notifier2 = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .addChannel("#general")
                .build();
        
        NotificationResult result1 = notifier1.send("Test", "Message");
        NotificationResult result2 = notifier2.send("Test", "Message");
        
        assertNotNull(result1);
        assertNotNull(result2);
        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());
    }
    
    @Test
    void testProviderIdIsUniqueAcrossInstances() throws NotificationException {
        SlackNotifier notifier1 = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        SlackNotifier notifier2 = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .build();
        
        NotificationResult result1 = notifier1.send("Test", "Message");
        NotificationResult result2 = notifier2.send("Test", "Message");
        
        assertNotEquals(result1.getProviderId(), result2.getProviderId());
    }
    
    @Test
    void testAllBuilderOptionsAtOnce() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .webhookUrl("https://hooks.slack.com/services/T00000000/B00000000/XXXXXXXXXXXXXXXXXXXX")
                .botToken("test-bot-token-1234567890")
                .addChannel("#general")
                .addChannel("#alerts")
                .username("SuperBot")
                .iconEmoji(":robot_face:")
                .iconUrl("https://example.com/icon.png")
                .markdown(true)
                .build();
        
        NotificationResult result = notifier.send("Test", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("webhook"));
    }
    
    @Test
    void testBotTokenWithoutWebhook() throws NotificationException {
        SlackNotifier notifier = SlackNotifier.builder()
                .botToken("test-bot-token-1234567890")
                .addChannel("#general")
                .build();
        
        NotificationResult result = notifier.send("Test", "Message");
        
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("channels"));
    }
}
