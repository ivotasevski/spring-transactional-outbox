package com.ivotasevski.transoutbox.lib;

import com.ivotasevski.transoutbox.lib.api.OutboxHandler;
import com.ivotasevski.transoutbox.lib.api.OutboxPayload;
import com.ivotasevski.transoutbox.lib.api.OutboxType;
import com.ivotasevski.transoutbox.lib.model.Outbox;
import com.ivotasevski.transoutbox.lib.model.OutboxStatus;
import com.ivotasevski.transoutbox.lib.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class OutboxService {

    private final OutboxRepository outboxRepository;
    private final OutboxHandlerRegistry handlerRegistry;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Outbox> findAndLockNextBatchForProcessing() {
        List<Outbox> batch = outboxRepository.findBatchForProcessing(5);
        batch.forEach(outbox -> outbox.setStatus(OutboxStatus.IN_PROGRESS));
        outboxRepository.saveAll(batch);
        return batch;
    }

    public Outbox saveOutbox(OutboxType type, OutboxPayload payload) {

        OutboxHandler handler = handlerRegistry.getHandler(type);

        Outbox outbox = new Outbox();
        outbox.setType(type.getType());
        outbox.setPayload(handler.serialize(payload));
        outbox.setStatus(OutboxStatus.PENDING);
        return outboxRepository.save(outbox);
    }


}
