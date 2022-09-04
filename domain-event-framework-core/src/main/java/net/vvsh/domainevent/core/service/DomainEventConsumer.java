package net.vvsh.domainevent.core.service;

import net.vvsh.domainevent.core.domain.DomainEvent;
import net.vvsh.domainevent.core.domain.DomainEventEnvelope;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
public class DomainEventConsumer {

    private final DomainEventService service;

    @EventListener
    public void consume(DomainEventEnvelope<DomainEvent> domainEventEnvelope) {
        service.save(domainEventEnvelope);
    }
}
