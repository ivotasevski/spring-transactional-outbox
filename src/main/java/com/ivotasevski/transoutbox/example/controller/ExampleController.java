package com.ivotasevski.transoutbox.example.controller;

import com.ivotasevski.transoutbox.example.dto.ResponseDto;
import com.ivotasevski.transoutbox.example.service.ExampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@RestController
public class ExampleController {

    private final ExampleService exampleService;

    @PostMapping("/outbox/immediate")
    CompletableFuture<ResponseDto> outboxImmediateExecution() {
//        return exampleService.addOutboxAndProcessImmediately();

        return exampleService.processOutbox();
    }

    @GetMapping("test")
    CompletableFuture<ResponseDto> test() {
        return exampleService.processOutbox();
    }

    @PostMapping("/outbox/scheduled")
    ResponseDto outboxScheduledExecution() {
        return exampleService.addScheduledOutbox();
    }
}
