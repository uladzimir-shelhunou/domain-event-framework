package net.vvsh.domainevent.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface DomainEvent {

    /**
     * Domain event type.
     * Default value is class name w/o package.
     *
     * @return event type
     */
    @JsonIgnore
    default String getEventType() {
        return getClass().getSimpleName();
    }
}
