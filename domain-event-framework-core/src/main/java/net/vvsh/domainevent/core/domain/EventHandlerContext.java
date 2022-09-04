package net.vvsh.domainevent.core.domain;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.lang.NonNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EventHandlerContext<E extends DomainEvent> {

    private UUID eventId;
    private E domainEvent;
    private String aggregateType;
    private UUID aggregateId;
    private Long timestamp;

    @NonNull
    public String getDomainEventType() {
        return domainEvent.getEventType();
    }
}
