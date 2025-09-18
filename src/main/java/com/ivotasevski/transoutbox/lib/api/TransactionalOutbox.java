package com.ivotasevski.transoutbox.lib.api;

import com.ivotasevski.transoutbox.lib.OutboxProcessor;
import com.ivotasevski.transoutbox.lib.OutboxService;
import com.ivotasevski.transoutbox.lib.model.Outbox;
import com.ivotasevski.transoutbox.lib.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionalOutbox {

    private final OutboxService outboxService;
    private final OutboxProcessor outboxProcessor;
    private final OutboxRepository outboxRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRED)
    public CompletableFuture<Boolean> addAndProcessImmediately(OutboxType type, OutboxPayload payload) {

        Outbox outbox = outboxService.saveOutbox(type, payload);

        CompletableFuture<Boolean> resultHolder = new CompletableFuture<>();

        // Try to process the outbox after transaction commit, and return a processing result once resolved
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        try {
                            Outbox outboxToBeProcessed = outboxRepository.findById(outbox.getId()).get();
                            outboxProcessor.process(outboxToBeProcessed);
                            resultHolder.complete(true);
                        } catch (Exception e) {
                            resultHolder.completeExceptionally(e);
                        }
                    }
                }
        );

        return resultHolder;
    }

    @Transactional
    public void add(OutboxType type, OutboxPayload payload) {
        outboxService.saveOutbox(type, payload);
    }

    public void shutdown() {
        // gracefully shutdown implementation goes here
    }

    // cleanup
    // processPending
    // unblock items that are stuck in PROCESSING
}
