package net.vvsh.domainevent.core.service;

import static java.util.stream.Collectors.toList;

import net.vvsh.domainevent.core.config.DomainEventProperties;
import net.vvsh.domainevent.core.entity.OutboxEntity;
import net.vvsh.domainevent.core.entity.OutboxEntity_;
import net.vvsh.domainevent.core.repository.OutboxRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public class MessageRelayService {

    private final OutboxRepository repository;
    private final MessageProducer messageProducer;
    private final DomainEventProperties properties;

    @Scheduled(fixedDelayString = "${domain-event.core.outbox.message-relay.interval.ms}")
    @Transactional
    public void process() {
        List<OutboxEntity> outboxEntities = findUnprocessedEvents();
        if (outboxEntities.isEmpty()) {
            return;
        }

        outboxEntities.forEach(outboxEntity -> {
            messageProducer.send(outboxEntity);
            outboxEntity.setFulfillmentDate(Instant.now());
            repository.save(outboxEntity);
        });

        List<UUID> domainEventIds = outboxEntities.stream().map(OutboxEntity::getEventId).collect(toList());
        log.debug("Processed events with ids: {}", domainEventIds);
    }

    private List<OutboxEntity> findUnprocessedEvents() {
        DomainEventProperties.OutboxProperties outboxProperties = properties.getOutbox();
        PageRequest pageRequest = PageRequest.ofSize(outboxProperties.getMessageRelay().getBatchSize())
                .withSort(Sort.by(OutboxEntity_.SUBMITTED_DATE).ascending());

        return repository.findUnprocessedEntities(pageRequest);
    }

}
