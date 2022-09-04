package net.vvsh.domainevent.core.handler;

import net.vvsh.domainevent.core.domain.DomainEvent;
import net.vvsh.domainevent.core.domain.EventHandlerContext;
import net.vvsh.domainevent.core.service.EventLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public abstract class IdempotentDomainEventHandler<E extends DomainEvent>
    implements DomainEventHandler<E> {

    @Autowired
    private EventLogService eventLogService;

    @Override
    @Transactional
    public void handle(@NonNull EventHandlerContext<E> context) {
        if (isEventProcessed(context)) {
            log.info("Domain event with id = {} was already processed.", context.getEventId());
            return;
        }

        processEvent(context);
    }

    private void processEvent(EventHandlerContext<E> context) {
        boolean processed = false;

        try {
            handleEvent(context);
            processed = true;
        } catch (Exception e) {
            log.error("Error occurred while processing domain event with id = {}", context.getEventId());
            throw e;
        } finally {
            if (processed) {
                markEventAsProcessed(context);
            }
        }
    }

    private boolean isEventProcessed(EventHandlerContext<E> context) {
        return context.getAggregateType() != null
            && context.getEventId() != null
            && eventLogService.isEventProcessed(context.getAggregateType(), context.getEventId());
    }

    private void markEventAsProcessed(EventHandlerContext<E> context) {
        if (context.getAggregateType() != null && context.getEventId() != null) {
            eventLogService.markEventAsProcessed(context.getAggregateType(), context.getEventId());
        }
    }

    /**
     * Domain event handler.
     *
     * @param context event context
     */
    protected abstract void handleEvent(EventHandlerContext<E> context);
}
