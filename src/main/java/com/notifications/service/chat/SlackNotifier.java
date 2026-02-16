package com.notifications.service.chat;

import com.notifications.core.NotificationException;
import com.notifications.core.NotificationResult;
import com.notifications.core.Notifier;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Slack-specific notifier.
 * Supports webhooks, channels, and Slack-specific formatting.
 * 
 * Example usage:
 * <pre>
 * Notifier slack = SlackNotifier.builder()
 *     .webhookUrl("https://hooks.slack.com/services/...")
 *     .addChannel("#general")
 *     .username("NotificationBot")
 *     .iconEmoji(":bell:")
 *     .build();
 * 
 * // Subject becomes bold title, message becomes body
 * slack.send("System Alert", "Database connection restored");
 * </pre>
 */
@Slf4j
public class SlackNotifier implements Notifier {
    
    private final String webhookUrl;
    private final String botToken;
    private final List<String> channels;
    private final String username;
    private final String iconEmoji;
    private final String iconUrl;
    private final boolean markdown;
    
    private SlackNotifier(Builder builder) {
        this.webhookUrl = builder.webhookUrl;
        this.botToken = builder.botToken;
        this.channels = new ArrayList<>(builder.channels);
        this.username = builder.username;
        this.iconEmoji = builder.iconEmoji;
        this.iconUrl = builder.iconUrl;
        this.markdown = builder.markdown;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Adds a channel to send the notification to.
     * 
     * @param channel the channel name (with # prefix) or ID
     * @return this notifier for chaining
     */
    public SlackNotifier addChannel(String channel) {
        if (channel != null && !channel.trim().isEmpty()) {
            channels.add(channel.trim());
        }
        return this;
    }
    
    /**
     * Adds multiple channels to send the notification to.
     * 
     * @param channels the channel names or IDs
     * @return this notifier for chaining
     */
    public SlackNotifier addChannels(String... channels) {
        for (String channel : channels) {
            addChannel(channel);
        }
        return this;
    }
    
    @Override
    public NotificationResult send(String subject, String message) throws NotificationException {
        if (webhookUrl == null && (botToken == null || channels.isEmpty())) {
            throw new NotificationException(
                "Either webhook URL or (bot token + channels) must be configured");
        }
        
        // Format Slack message
        String slackMessage = formatSlackMessage(subject, message);
        
        String target = webhookUrl != null ? "webhook" : channels.size() + " channels";
        log.info("Sending Slack notification to {}: subject='{}'", target, subject);
        
        try {
            // Simulate Slack API call
            simulateSlackSend(slackMessage);
            
            String providerId = "slack-" + UUID.randomUUID();
            
            log.info("Slack notification sent successfully: providerId={}", providerId);
            
            return NotificationResult.builder()
                    .success(true)
                    .providerId(providerId)
                    .message(String.format("Slack notification sent to %s", target))
                    .build();
            
        } catch (Exception e) {
            log.error("Failed to send Slack notification: {}", e.getMessage());
            throw new NotificationException("Failed to send Slack notification", e);
        }
    }
    
    private String formatSlackMessage(String subject, String message) {
        StringBuilder sb = new StringBuilder();
        
        if (markdown && subject != null && !subject.trim().isEmpty()) {
            sb.append("*").append(subject).append("*\n");
        } else if (subject != null && !subject.trim().isEmpty()) {
            sb.append(subject).append("\n");
        }
        
        if (message != null && !message.trim().isEmpty()) {
            sb.append(message);
        }
        
        return sb.toString();
    }
    
    private void simulateSlackSend(String message) {
        // Simulate API call delay
        try {
            Thread.sleep(140 + (long) (Math.random() * 100));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.debug("Simulated Slack message: webhookUrl={}, channels={}, username={}, " +
                "iconEmoji={}, messageLength={}, markdown={}", 
                webhookUrl != null ? "configured" : "null", channels, username, 
                iconEmoji, message.length(), markdown);
    }
    
    /**
     * Builder for SlackNotifier
     */
    public static class Builder {
        private String webhookUrl;
        private String botToken;
        private List<String> channels = new ArrayList<>();
        private String username = "Notification Bot";
        private String iconEmoji;
        private String iconUrl;
        private boolean markdown = true;
        
        public Builder webhookUrl(String webhookUrl) {
            this.webhookUrl = webhookUrl;
            return this;
        }
        
        public Builder botToken(String botToken) {
            this.botToken = botToken;
            return this;
        }
        
        public Builder addChannel(String channel) {
            if (channel != null && !channel.trim().isEmpty()) {
                this.channels.add(channel.trim());
            }
            return this;
        }
        
        public Builder username(String username) {
            this.username = username;
            return this;
        }
        
        public Builder iconEmoji(String iconEmoji) {
            this.iconEmoji = iconEmoji;
            return this;
        }
        
        public Builder iconUrl(String iconUrl) {
            this.iconUrl = iconUrl;
            return this;
        }
        
        public Builder markdown(boolean markdown) {
            this.markdown = markdown;
            return this;
        }
        
        public SlackNotifier build() {
            if (webhookUrl == null && botToken == null) {
                throw new IllegalArgumentException(
                    "Either webhook URL or bot token must be provided");
            }
            if (webhookUrl == null && channels.isEmpty()) {
                throw new IllegalArgumentException(
                    "At least one channel must be specified when using bot token");
            }
            return new SlackNotifier(this);
        }
    }
}
