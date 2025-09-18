package com.ivotasevski.transoutbox.example.outbox;

import com.ivotasevski.transoutbox.lib.api.OutboxPayload;
import lombok.Data;

@Data
public class ExampleOutboxPayload implements OutboxPayload {
    private String data;
}
