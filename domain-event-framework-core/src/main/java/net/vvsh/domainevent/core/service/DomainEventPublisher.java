package net.vvsh.domainevent.core.service;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;

import net.vvsh.domainevent.core.domain.AggregateType;
import net.vvsh.domainevent.core.domain.DomainEvent;
import net.vvsh.domainevent.core.domain.DomainEventEnvelope;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional(propagation = MANDATORY)
    public void publish(@NonNull AggregateType aggregateType,
                        @NonNull UUID aggregateId,
                        @NonNull DomainEvent domainEvent) {
        DomainEventEnvelope<DomainEvent> eventEnvelope = buildEnvelope(aggregateType, aggregateId,
                                                                       domainEvent);
        applicationEventPublisher.publishEvent(eventEnvelope);
    }

    @Transactional(propagation = MANDATORY)
    public void publish(@NonNull AggregateType aggregateType,
                        @NonNull UUID aggregateId,
                        @NonNull List<DomainEvent> domainEvents) {
        domainEvents.forEach(domainEvent -> publish(aggregateType, aggregateId, domainEvent));
    }

    @NonNull
    private DomainEventEnvelope<DomainEvent> buildEnvelope(@NonNull AggregateType aggregateType,
                                                           @NonNull UUID aggregateId,
                                                           @NonNull DomainEvent domainEvent) {
        return DomainEventEnvelope.builder()
            .aggregateType(aggregateType.getName())
            .aggregateId(aggregateId)
            .eventType(domainEvent.getEventType())
            .payload(domainEvent)
            .build();
    }
}
