package com.ivotasevski.transoutbox.example.service;

import com.ivotasevski.transoutbox.example.domain.Example;
import com.ivotasevski.transoutbox.example.dto.ResponseDto;
import com.ivotasevski.transoutbox.example.outbox.ExampleOutboxPayload;
import com.ivotasevski.transoutbox.example.outbox.ExampleOutboxType;
import com.ivotasevski.transoutbox.example.respository.ExampleRepository;
import com.ivotasevski.transoutbox.lib.api.TransactionalOutbox;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExampleService {

    private final ExampleRepository exampleRepository;
    private final TransactionalOutbox transactionalOutbox;
    private final TransactionTemplate transactionTemplate;

    @Transactional
    @SneakyThrows
    public ResponseDto addOutboxAndProcessImmediately(String message) {

        // wrap business logic and outbox creation in the same transaction
        CompletableFuture<Boolean> outboxResult = transactionTemplate.execute(status -> {

            // sample business logic - START
            var example = new Example();
            example.setValue(message);

            exampleRepository.save(example);
            // sample business logic - END


            // prepare outbox save as part of the current transaction and immediately execute it after the transaction is committed
            ExampleOutboxPayload payload = new ExampleOutboxPayload();
            payload.setData(example.getValue());

            return transactionalOutbox.addAndProcessImmediately(ExampleOutboxType.EXAMPLE, payload);
        });

        // this is a blocking statement. It will return only when the outbox is processed or an error occurs
        // it must be outside the transaction which created the outbox
        boolean isSuccessful = outboxResult.get();

        ResponseDto responseDto = new ResponseDto();
        if (isSuccessful) {
            responseDto.setMessage("Outbox processed immediately");
        } else {
            responseDto.setMessage("Outbox did NOT process immediately. Will be processed later by the scheduler");
        }

        return responseDto;
    }

    public ResponseDto addScheduledOutbox(String message) {

        var example = new Example();
        example.setValue(message);

        exampleRepository.save(example);

        ExampleOutboxPayload payload = new ExampleOutboxPayload();
        payload.setData(example.getValue());

        transactionalOutbox.add(ExampleOutboxType.EXAMPLE, payload);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("Outbox is scheduled for processing. Message: " + message);
        return responseDto;
    }
}
