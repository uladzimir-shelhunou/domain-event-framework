package net.vvsh.domainevent.core.handler;

import net.vvsh.domainevent.core.domain.DomainEvent;
import org.springframework.lang.NonNull;

public interface LocalDomainEventHandler<E extends DomainEvent>
        extends DomainEventHandler<E> {

    @NonNull
    @Override
    default HandlerType getHandlerType() {
        return HandlerType.LOCAL;
    }
}
