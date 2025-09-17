package com.ivotasevski.transoutbox.lib.exception;

import com.ivotasevski.transoutbox.lib.model.Outbox;

public class OutboxProcessingException extends OutboxException {

    private static String constructMessage(Outbox outbox) {
        return "Failed to process outbox with id: " + outbox.getId() + ", type: " + outbox.getType();
    }

    public OutboxProcessingException(Outbox outbox) {
        super(constructMessage(outbox));
    }

    public OutboxProcessingException(Outbox outbox, Throwable cause) {
        super(constructMessage(outbox), cause);
    }
}
