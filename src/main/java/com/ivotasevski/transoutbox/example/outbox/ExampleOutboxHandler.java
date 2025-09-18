package com.ivotasevski.transoutbox.example.outbox;

import com.ivotasevski.transoutbox.lib.api.OutboxHandler;
import com.ivotasevski.transoutbox.lib.api.OutboxPayload;
import com.ivotasevski.transoutbox.lib.api.OutboxType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Component
public class ExampleOutboxHandler implements OutboxHandler {
    @Override
    public OutboxType getSupportedType() {
        return ExampleOutboxType.EXAMPLE;
    }

    @Override
    public String serialize(OutboxPayload payload) {
        return ((ExampleOutboxPayload) payload).getData();
    }

    @Override
    public Instant getNextExecutionTime(long currentRetries) {
        return Instant.now().plusSeconds(5);
    }

    @Override
    public boolean hasReachedMaxRetries(long retries) {
        return false;
    }

    @SneakyThrows
    @Override
    public void handle(String payload) {
        // mimic remote call
        Thread.sleep(3000);
        log.info("Handled example payload: {}", payload);
    }

    @Override
    public void handleFailure(String payload) {
        log.error("Error handling example payload: {}", payload);
    }

    @Override
    public Duration getRetentionDuration() {
        return Duration.ofDays(1);
    }
}
