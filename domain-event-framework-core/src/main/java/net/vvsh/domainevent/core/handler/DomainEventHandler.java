package net.vvsh.domainevent.core.handler;

import net.vvsh.domainevent.core.domain.AggregateType;
import net.vvsh.domainevent.core.domain.DomainEvent;
import net.vvsh.domainevent.core.domain.EventHandlerContext;
import org.springframework.lang.NonNull;

public interface DomainEventHandler<E extends DomainEvent> {

    /**
     * Handler domain event.
     *
     * @param context event handler context
     */
    void handle(@NonNull EventHandlerContext<E> context);

    /**
     * Type of aggregate domain event belongs to.
     *
     * @return type of aggregate
     */
    @NonNull
    AggregateType getAggregateType();

    /**
     * Domain event class this handler is able to process.
     *
     * @return class of domain event
     */
    @NonNull
    Class<E> getEventClass();

    /**
     * Type of the handler.
     *
     * By default, handler type is REMOTE.
     * Remote handlers will be executed in a separate thread upon receiving messages from the event bus.
     *
     * @return handler type
     */
    @NonNull
    default HandlerType getHandlerType() {
        return HandlerType.REMOTE;
    }

}
