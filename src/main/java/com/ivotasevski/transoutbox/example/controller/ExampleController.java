package com.ivotasevski.transoutbox.example.controller;

import com.ivotasevski.transoutbox.example.dto.RequestDto;
import com.ivotasevski.transoutbox.example.dto.ResponseDto;
import com.ivotasevski.transoutbox.example.service.ExampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ExampleController {

    private final ExampleService exampleService;

    @PostMapping("/outbox/immediate")
    ResponseDto outboxImmediateExecution(@RequestBody RequestDto request) {
        return exampleService.addOutboxAndProcessImmediately(request.getData());
    }

    @PostMapping("/outbox/scheduled")
    ResponseDto outboxScheduledExecution(@RequestBody RequestDto request) {
        return exampleService.addScheduledOutbox(request.getData());
    }
}
