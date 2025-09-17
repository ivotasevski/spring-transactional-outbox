package com.ivotasevski.transoutbox.lib;

import com.ivotasevski.transoutbox.lib.api.OutboxHandler;
import com.ivotasevski.transoutbox.lib.exception.OutboxProcessingException;
import com.ivotasevski.transoutbox.lib.model.Outbox;
import com.ivotasevski.transoutbox.lib.model.OutboxStatus;
import com.ivotasevski.transoutbox.lib.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;

@RequiredArgsConstructor
@Slf4j
@Component
public class OutboxProcessor {

    private static final String LOGGER_PREFIX = "[OutboxProcessor] ";

    private final OutboxHandlerRegistry outboxHandlerRegistry;

    private final TransactionTemplate outboxTransactionTemplate;
    private final OutboxRepository outboxRepository;


    public void process(Outbox outbox) {

        // at this point, outbox is marked as IN_PROGRESS

        OutboxHandler handler = outboxHandlerRegistry.getHandler(outbox.getType());

        try {
            log.info(LOGGER_PREFIX + "Processing outbox item with id: '{}' and type: '{}'", outbox.getId(), outbox.getType());
            handler.handle(outbox.getPayload());
            handleSuccess(handler, outbox);

        } catch (Exception e) {
            if (handler.hasReachedMaxRetries(outbox.getRetries())) {
                handleTerminalFailure(handler, outbox, e);
            } else {
                handleRetryableFailure(handler, outbox, e);
            }
            throw new OutboxProcessingException(outbox, e);
        } finally {
            outboxTransactionTemplate.executeWithoutResult(status -> {
                outboxRepository.save(outbox);
            });
        }
    }

    private void handleSuccess(OutboxHandler handler, Outbox outbox) {
        outbox.setStatus(OutboxStatus.COMPLETED);
        outbox.setDeleteAfter(Instant.now().plus(handler.getRetentionDuration()));
    }

    private void handleRetryableFailure(OutboxHandler handler, Outbox outbox, Exception exception) {
        outbox.setNextRunNotBefore(handler.getNextExecutionTime(outbox.getRetries()));
        outbox.setRetries(outbox.getRetries() + 1);
        outbox.setStatus(OutboxStatus.PENDING);
        log.info(
                LOGGER_PREFIX + "Failure handling outbox item with id: {} and type: {}. " +
                        "Updated retries ({}) and next run is not before {}.", outbox.getId(), outbox.getType(), outbox.getRetries(), outbox.getNextRunNotBefore(),
                exception
        );
    }

    private void handleTerminalFailure(OutboxHandler handler, Outbox outbox, Exception exception) {
        log.error(LOGGER_PREFIX + "Failure handling outbox item with id: {} and type: {}. " +
                        "Item reached max-retries ({}), delegating failure to handler.", outbox.getId(), outbox.getType(), outbox.getRetries(),
                exception
        );
        outbox.setStatus(OutboxStatus.FAILED);
        handler.handleFailure(outbox.getPayload());
    }
}
