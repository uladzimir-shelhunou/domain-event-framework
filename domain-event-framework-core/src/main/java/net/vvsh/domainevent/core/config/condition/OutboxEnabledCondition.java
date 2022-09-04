package net.vvsh.domainevent.core.config.condition;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

public class OutboxEnabledCondition extends AllNestedConditions {

    public OutboxEnabledCondition() {
        super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty(prefix = "domain-event.core.outbox", name = "enabled", havingValue = "true")
    static class OutboxEnabled {
    }

}
