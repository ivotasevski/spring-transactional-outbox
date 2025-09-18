package com.ivotasevski.transoutbox.example.outbox;

import com.ivotasevski.transoutbox.lib.api.OutboxType;

public enum ExampleOutboxType implements OutboxType {

    EXAMPLE;

    @Override
    public String getType() {
        return this.name();
    }
}
