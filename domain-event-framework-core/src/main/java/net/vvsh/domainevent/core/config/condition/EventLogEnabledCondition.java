package net.vvsh.domainevent.core.config.condition;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

public class EventLogEnabledCondition extends AllNestedConditions {

    public EventLogEnabledCondition() {
        super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty(prefix = "domain-event.core.event-log", name = "enabled", havingValue = "true")
    static class EventLogEnabled {
    }

}
