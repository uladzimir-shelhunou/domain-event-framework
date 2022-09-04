package net.vvsh.domainevent.core.domain;

import static java.util.Objects.requireNonNull;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
public class DomainEventEnvelope<E extends DomainEvent> {

    private String eventType;
    private String aggregateType;
    private UUID aggregateId;
    private E payload;

    @Builder
    public DomainEventEnvelope(String aggregateType, UUID aggregateId, String eventType, E payload) {
        this.aggregateType = requireNonNull(aggregateType);
        this.aggregateId = requireNonNull(aggregateId);
        this.eventType = requireNonNull(eventType);
        this.payload = requireNonNull(payload);
    }
}
