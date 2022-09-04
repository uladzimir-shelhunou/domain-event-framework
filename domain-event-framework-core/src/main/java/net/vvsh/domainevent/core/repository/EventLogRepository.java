package net.vvsh.domainevent.core.repository;

import net.vvsh.domainevent.core.entity.EventLogEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventLogRepository extends JpaRepository<EventLogEntity, UUID> {

    boolean existsByAggregateTypeAndEventId(String aggregateType, UUID eventId);
}
