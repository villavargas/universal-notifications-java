package com.notifications.core;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Notify is the central class for managing notification services and sending messages.
 * Inspired by Go's "notify" library design pattern.
 * 
 * This class:
 * - Composes multiple Notifier implementations
 * - Sends notifications to all configured notifiers (broadcast)
 * - Supports both sync and async operations
 * - Can be disabled without removing notifiers
 * 
 * Example usage:
 * <pre>
 * // Create notifiers
 * Notifier emailNotifier = new EmailNotifier(...);
 * Notifier smsNotifier = new SmsNotifier(...);
 * 
 * // Compose them
 * Notify notify = Notify.create()
 *     .use(emailNotifier)
 *     .use(smsNotifier);
 * 
 * // Send to all channels
 * notify.send("Alert", "System is down!");
 * </pre>
 */
@Slf4j
public class Notify implements Notifier {
    
    private final List<Notifier> notifiers;
    private final ExecutorService executorService;
    private boolean disabled;
    
    /**
     * Creates a new Notify instance with default settings.
     */
    public Notify() {
        this.notifiers = new ArrayList<>();
        this.executorService = Executors.newVirtualThreadPerTaskExecutor(); // Java 21 virtual threads
        this.disabled = false;
    }
    
    /**
     * Creates a new Notify instance.
     * 
     * @return new Notify instance
     */
    public static Notify create() {
        return new Notify();
    }
    
    /**
     * Creates a new disabled Notify instance (no-op mode).
     * Useful for testing or feature flags.
     * 
     * @return new disabled Notify instance
     */
    public static Notify createDisabled() {
        Notify notify = new Notify();
        notify.disable();
        return notify;
    }
    
    /**
     * Creates a new Notify instance with the given notifiers.
     * 
     * @param notifiers the notifiers to use
     * @return new Notify instance with configured notifiers
     */
    public static Notify withNotifiers(Notifier... notifiers) {
        Notify notify = new Notify();
        notify.use(notifiers);
        return notify;
    }
    
    /**
     * Adds a notifier to this Notify instance.
     * 
     * @param notifier the notifier to add
     * @return this instance for chaining
     */
    public Notify use(Notifier notifier) {
        if (notifier != null) {
            notifiers.add(notifier);
            log.debug("Added notifier: {}", notifier.getClass().getSimpleName());
        }
        return this;
    }
    
    /**
     * Adds multiple notifiers to this Notify instance.
     * 
     * @param notifiers the notifiers to add
     * @return this instance for chaining
     */
    public Notify use(Notifier... notifiers) {
        if (notifiers != null) {
            Arrays.stream(notifiers)
                .filter(n -> n != null)
                .forEach(this::use);
        }
        return this;
    }
    
    /**
     * Disables this Notify instance.
     * When disabled, send operations will return immediately without sending.
     * 
     * @return this instance for chaining
     */
    public Notify disable() {
        this.disabled = true;
        log.info("Notify instance disabled");
        return this;
    }
    
    /**
     * Enables this Notify instance (default state).
     * 
     * @return this instance for chaining
     */
    public Notify enable() {
        this.disabled = false;
        log.info("Notify instance enabled");
        return this;
    }
    
    /**
     * Checks if this Notify instance is disabled.
     * 
     * @return true if disabled, false otherwise
     */
    public boolean isDisabled() {
        return disabled;
    }
    
    /**
     * Gets the number of configured notifiers.
     * 
     * @return number of notifiers
     */
    public int getNotifierCount() {
        return notifiers.size();
    }
    
    /**
     * Sends a notification to all configured notifiers synchronously.
     * 
     * This method will:
     * - Return immediately if disabled
     * - Send to all notifiers in parallel
     * - Collect all errors and throw a composite exception if any fail
     * - Return a composite result with all individual results
     * 
     * @param subject the subject/title of the notification
     * @param message the main content/body of the notification
     * @return composite result containing all individual results
     * @throws NotificationException if any notifier fails
     */
    @Override
    public NotificationResult send(String subject, String message) throws NotificationException {
        if (disabled) {
            log.debug("Notify is disabled, skipping send operation");
            return NotificationResult.disabled();
        }
        
        if (notifiers.isEmpty()) {
            log.warn("No notifiers configured, nothing to send");
            return NotificationResult.noNotifiers();
        }
        
        log.info("Sending notification to {} notifiers: subject='{}', message length={}",
                notifiers.size(), subject, message != null ? message.length() : 0);
        
        List<NotificationResult> results = new ArrayList<>();
        List<Exception> errors = new ArrayList<>();
        
        // Send to all notifiers in parallel using virtual threads
        List<CompletableFuture<NotificationResult>> futures = notifiers.stream()
            .map(notifier -> CompletableFuture.supplyAsync(() -> {
                try {
                    return notifier.send(subject, message);
                } catch (Exception e) {
                    log.error("Notifier {} failed: {}", 
                            notifier.getClass().getSimpleName(), e.getMessage());
                    throw new RuntimeException(e);
                }
            }, executorService))
            .collect(Collectors.toList());
        
        // Wait for all and collect results
        for (CompletableFuture<NotificationResult> future : futures) {
            try {
                results.add(future.join());
            } catch (Exception e) {
                errors.add(e);
            }
        }
        
        // If all failed, throw exception
        if (!errors.isEmpty() && results.isEmpty()) {
            Exception firstError = errors.get(0);
            if (firstError instanceof NotificationException) {
                throw (NotificationException) firstError;
            }
            throw new NotificationException("All notifiers failed", firstError);
        }
        
        // If some failed, log warnings
        if (!errors.isEmpty()) {
            log.warn("{}/{} notifiers failed", errors.size(), notifiers.size());
        }
        
        log.info("Successfully sent to {}/{} notifiers", results.size(), notifiers.size());
        
        return NotificationResult.composite(results, notifiers.size());
    }
    
    /**
     * Sends a notification to all configured notifiers asynchronously.
     * 
     * @param subject the subject/title of the notification
     * @param message the main content/body of the notification
     * @return CompletableFuture with the result
     */
    public CompletableFuture<NotificationResult> sendAsync(String subject, String message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return send(subject, message);
            } catch (NotificationException e) {
                throw new RuntimeException(e);
            }
        }, executorService);
    }
    
    /**
     * Closes this Notify instance and releases resources.
     * Should be called when done using this instance.
     */
    public void close() {
        executorService.shutdown();
        log.info("Notify instance closed");
    }
}
