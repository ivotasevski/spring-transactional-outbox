package com.ivotasevski.transoutbox.example.service;

import com.ivotasevski.transoutbox.example.dto.ResponseDto;
import com.ivotasevski.transoutbox.lib.model.Outbox;
import com.ivotasevski.transoutbox.lib.model.OutboxStatus;
import com.ivotasevski.transoutbox.lib.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExampleService {

    private final OutboxRepository outboxRepository;
    private final TransactionTemplate transactionTemplate;

    public ResponseDto addOutboxAndProcessImmediately() {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Outbox processed immediately");
        return responseDto;
    }

    public ResponseDto addScheduledOutbox() {
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Outbox processed immediately");
        return responseDto;
    }

    //    @Async
    @Transactional
    public CompletableFuture<ResponseDto> processOutbox() {

        var outbox = new Outbox();
        outbox.setType("MyType");
        outbox.setStatus(OutboxStatus.IN_PROGRESS);
        outbox.setPayload("Hello");

        outboxRepository.save(outbox);

        CompletableFuture<ResponseDto> result = new CompletableFuture<>();

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {

                        transactionTemplate.executeWithoutResult(status -> {
                            var outbox = new Outbox();
                            outbox.setType("AfterCommit");
                            outbox.setStatus(OutboxStatus.IN_PROGRESS);
                            outbox.setPayload("Hello from AfterCommit");

                            outboxRepository.save(outbox);
                        });

                        log.info("In 'afterCommit' callback.");

                        ResponseDto responseDto = new ResponseDto();
                        responseDto.setMessage("Async result");
                        result.complete(responseDto);
                    }
                });

        return result;
    }
}
