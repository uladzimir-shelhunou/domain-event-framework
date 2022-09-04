package net.vvsh.domainevent.core.entity;

import net.vvsh.domainevent.core.domain.DomainEvent;
import net.vvsh.domainevent.core.entity.converter.DomainEventConverter;
import java.time.Instant;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "outbox")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEntity {

    @Id
    @Column(name = "event_id", nullable = false)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID eventId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Convert(converter = DomainEventConverter.class)
    @Column(name = "payload")
    private DomainEvent payload;

    @Column(name = "submitted_date")
    private Instant submittedDate;

    @Column(name = "fulfillment_date")
    private Instant fulfillmentDate;
}
