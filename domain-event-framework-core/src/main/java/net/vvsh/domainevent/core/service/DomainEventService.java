package net.vvsh.domainevent.core.service;

import net.vvsh.domainevent.core.domain.DomainEvent;
import net.vvsh.domainevent.core.domain.DomainEventEnvelope;
import net.vvsh.domainevent.core.entity.OutboxEntity;
import net.vvsh.domainevent.core.mapper.DomainEventMapper;
import net.vvsh.domainevent.core.repository.OutboxRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DomainEventService {

    private final OutboxRepository repository;
    private final DomainEventMapper mapper;

    void save(DomainEventEnvelope<DomainEvent> domainEvent) {
        save(List.of(domainEvent));
    }

    void save(List<DomainEventEnvelope<DomainEvent>> domainEvents) {
        List<OutboxEntity> outboxEntities = mapper.toEntityList(domainEvents);
        repository.saveAll(outboxEntities);
    }
}
