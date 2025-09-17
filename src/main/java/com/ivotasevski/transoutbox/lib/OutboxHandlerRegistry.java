package com.ivotasevski.transoutbox.lib;

import com.ivotasevski.transoutbox.lib.api.OutboxHandler;
import com.ivotasevski.transoutbox.lib.api.OutboxType;
import com.ivotasevski.transoutbox.lib.exception.OutboxException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OutboxHandlerRegistry {

    private final Map<String, OutboxHandler> handlers = new HashMap<>();

    public OutboxHandlerRegistry(List<OutboxHandler> handlers) {
        for (OutboxHandler handler : handlers) {
            if (this.handlers.containsKey(handler.getSupportedType())) {
                throw new OutboxException("Duplicate handler for type: " + handler.getSupportedType() + " found.");
            }
            this.handlers.put(handler.getSupportedType().getType(), handler);
        }
    }

    public OutboxHandler getHandler(OutboxType type) {
        return getHandler(type.getType());
    }

    public OutboxHandler getHandler(String type) {
        return handlers.get(type);
    }
}
