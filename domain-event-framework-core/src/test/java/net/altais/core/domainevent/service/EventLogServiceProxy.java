package net.altais.core.domainevent.service;

import net.vvsh.domainevent.core.domain.AggregateType;
import lombok.RequiredArgsConstructor;
import net.vvsh.domainevent.core.service.EventLogService;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
public class EventLogServiceProxy {

    private final EventLogService eventLogService;

    @Transactional
    public boolean isEventProcessed(AggregateType aggregateType, UUID eventId) {
        return eventLogService.isEventProcessed(aggregateType.getName(), eventId);
    }
}
