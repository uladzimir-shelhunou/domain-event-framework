package net.vvsh.domainevent.core.service.local;

import net.vvsh.domainevent.core.domain.DomainEvent;
import net.vvsh.domainevent.core.domain.DomainEventEnvelope;
import net.vvsh.domainevent.core.domain.EventHandlerContext;
import net.vvsh.domainevent.core.handler.DomainEventHandler;
import net.vvsh.domainevent.core.handler.DomainEventHandlerRegistry;
import net.vvsh.domainevent.core.handler.HandlerType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

@Slf4j
@RequiredArgsConstructor
public class LocalDomainEventProcessor<E extends DomainEvent> {

    private final DomainEventHandlerRegistry<E> registry;

    @EventListener
    public void handle(DomainEventEnvelope<E> envelope) {
        Class<? extends DomainEvent> eventClass = envelope.getPayload().getClass();
        List<DomainEventHandler<E>> handlers = registry.getHandlers(HandlerType.LOCAL, envelope.getAggregateType(),
                                                                    eventClass);

        EventHandlerContext<E> context = buildContext(envelope);

        for (DomainEventHandler<E> handler : handlers) {
            try {
                handler.handle(context);
            } catch (Exception e) {
                log.error("Error occurred during processing of event with type: %s"
                        .formatted(envelope.getEventType()), e);

                throw e;
            }
        }
    }

    private EventHandlerContext<E> buildContext(DomainEventEnvelope<E> envelope) {
        return EventHandlerContext.<E>builder()
                .aggregateType(envelope.getAggregateType())
                .aggregateId(envelope.getAggregateId())
                .domainEvent(envelope.getPayload())
                .build();
    }

}
