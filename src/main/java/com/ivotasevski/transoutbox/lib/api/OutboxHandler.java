package com.ivotasevski.transoutbox.lib.api;

import java.time.Duration;
import java.time.Instant;

public interface OutboxHandler {

    /**
     * Returns the type of the outbox item that this handler can handle.
     */
    public OutboxType getSupportedType();

    /**
     * Serializes the payload into a string.
     *
     * @param payload the payload of the outbox item
     * @return the serialized payload
     */
    String serialize(OutboxPayload payload);

    /**
     * Returns the next execution time for the outbox item.
     *
     * @param currentRetries the number of retries that have been performed
     * @return the next execution time
     */
    Instant getNextExecutionTime(long currentRetries);

    /**
     * Returns true if the outbox item has reached the maximum number of retries.
     *
     * @param retries the number of retries that have been performed
     * @return true if the outbox item has reached the maximum number of retries
     */
    boolean hasReachedMaxRetries(long retries);

    /**
     * Handles the outbox item.
     *
     * @param payload the payload of the outbox item
     */
    void handle(String payload);

    /**
     * Handles the outbox item when it has reached the maximum number of retries.
     *
     * @param payload the payload of the outbox item
     */
    void handleFailure(String payload);

    /**
     * Returns the amount of time that the outbox items of this handler's type should be retained.
     * The outbox items will be deleted after this amount of time has passed after their completion.
     *
     * @return the retention duration of the outbox items
     */
    Duration getRetentionDuration();
}
