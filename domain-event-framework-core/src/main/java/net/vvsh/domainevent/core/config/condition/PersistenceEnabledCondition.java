package net.vvsh.domainevent.core.config.condition;

import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.context.annotation.Conditional;

public class PersistenceEnabledCondition extends AnyNestedCondition {

    public PersistenceEnabledCondition() {
        super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @Conditional(EventLogEnabledCondition.class)
    static class EventLogEnabled {
    }

    @Conditional(OutboxEnabledCondition.class)
    static class OutboxEnabled {
    }
}
