package com.ivotasevski.transoutbox.lib;

import com.ivotasevski.transoutbox.lib.model.Outbox;
import com.ivotasevski.transoutbox.lib.model.OutboxStatus;
import com.ivotasevski.transoutbox.lib.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.Executor;

@Slf4j
@RequiredArgsConstructor
@Component
public class OutboxAsyncProcessingScheduler {

    private final OutboxRepository outboxRepository;
    private final OutboxProcessor outboxProcessor;

    @Qualifier("outboxExecutor")
    private final Executor outboxExecutor;

    private final TransactionTemplate transactionTemplate;

    @Scheduled(cron = "0 * * * * *")
    public void processOutboxMessages() {

        log.info("Scheduled job for processing outbox items started.");

        // lock, fetch, mark running and release lock
        List<Outbox> outboxList = transactionTemplate.execute(status -> {
            List<Outbox> outboxItems = outboxRepository.findBatchForProcessing(10);
            outboxItems.forEach(outbox -> outbox.setStatus(OutboxStatus.RUNNING));
            return outboxRepository.saveAll(outboxItems);
        });

        log.info("Fetched {} outbox items for processing.", outboxList.size());

        for (Outbox outbox : outboxList) {
            outboxExecutor.execute(() -> {
                outboxProcessor.process(outbox);
            });
        }
    }
}
