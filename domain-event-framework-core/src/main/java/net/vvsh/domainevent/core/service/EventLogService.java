package net.vvsh.domainevent.core.service;

import static org.springframework.transaction.annotation.Propagation.MANDATORY;

import net.vvsh.domainevent.core.entity.EventLogEntity;
import net.vvsh.domainevent.core.repository.EventLogRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public class EventLogService {

    private final EventLogRepository repository;

    @Transactional(propagation = MANDATORY, readOnly = true)
    public boolean isEventProcessed(@NonNull String aggregateType, @NonNull UUID domainEventId) {
        return repository.existsByAggregateTypeAndEventId(aggregateType, domainEventId);
    }

    @Transactional(propagation = MANDATORY)
    public void markEventAsProcessed(@NonNull String aggregateType, @NonNull UUID domainEventId) {
        EventLogEntity entity = new EventLogEntity();
        entity.setAggregateType(aggregateType);
        entity.setEventId(domainEventId);
        entity.setProcessedDate(Instant.now());
        repository.save(entity);
    }
}
