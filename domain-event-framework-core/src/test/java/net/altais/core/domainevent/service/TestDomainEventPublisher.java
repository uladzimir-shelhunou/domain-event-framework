package net.altais.core.domainevent.service;

import net.vvsh.domainevent.core.domain.AggregateType;
import net.vvsh.domainevent.core.domain.DomainEvent;
import lombok.RequiredArgsConstructor;
import net.vvsh.domainevent.core.service.DomainEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestDomainEventPublisher {

    private final DomainEventPublisher domainEventPublisher;

    @Transactional
    public void publish(AggregateType aggregateType, UUID aggregateId, DomainEvent domainEvent) {
        domainEventPublisher.publish(aggregateType, aggregateId, domainEvent);
    }

    @Transactional
    public void publish(AggregateType aggregateType, UUID aggregateId, List<DomainEvent> domainEvents) {
        domainEventPublisher.publish(aggregateType, aggregateId, domainEvents);
    }

}
