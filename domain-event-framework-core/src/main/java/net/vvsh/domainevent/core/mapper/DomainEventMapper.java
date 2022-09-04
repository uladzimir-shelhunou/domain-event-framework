package net.vvsh.domainevent.core.mapper;

import net.vvsh.domainevent.core.domain.DomainEvent;
import net.vvsh.domainevent.core.domain.DomainEventEnvelope;
import net.vvsh.domainevent.core.entity.OutboxEntity;
import java.time.Instant;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface DomainEventMapper {

    @Mapping(target = "eventId", ignore = true)
    OutboxEntity toEntity(DomainEventEnvelope<DomainEvent> domainEvent);

    List<OutboxEntity> toEntityList(List<DomainEventEnvelope<DomainEvent>> domainEvents);

    @AfterMapping
    default void afterToEntity(@MappingTarget OutboxEntity outboxEntity) {
        if (outboxEntity.getSubmittedDate() == null) {
            outboxEntity.setSubmittedDate(Instant.now());
        }
    }
}
