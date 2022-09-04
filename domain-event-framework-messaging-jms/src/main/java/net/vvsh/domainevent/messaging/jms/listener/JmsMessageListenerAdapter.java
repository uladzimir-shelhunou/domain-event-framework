package net.vvsh.domainevent.messaging.jms.listener;

import net.vvsh.domainevent.core.domain.DomainEvent;
import net.vvsh.domainevent.core.domain.EventHandlerContext;
import net.vvsh.domainevent.core.handler.DomainEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.support.JmsHeaderMapper;
import org.springframework.jms.support.SimpleJmsHeaderMapper;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.messaging.MessageHeaders;

import java.util.List;
import javax.jms.Message;
import javax.jms.MessageListener;
import static net.vvsh.domainevent.messaging.jms.util.JmsMessageUtils.getAggregateId;
import static net.vvsh.domainevent.messaging.jms.util.JmsMessageUtils.getAggregateType;
import static net.vvsh.domainevent.messaging.jms.util.JmsMessageUtils.getEventId;
import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
public class JmsMessageListenerAdapter<E extends DomainEvent> implements MessageListener {

    private static final JmsHeaderMapper JMS_HEADER_MAPPER = new SimpleJmsHeaderMapper();

    private final List<DomainEventHandler<E>> handlers;
    private final MessageConverter messageConverter;

    @Override
    public void onMessage(Message message) {
        try {
            Object payload = messageConverter.fromMessage(message);
            MessageHeaders headers = JMS_HEADER_MAPPER.toHeaders(message);

            List<DomainEventHandler<E>> suitableHandlers = handlers.stream()
                    .filter(handler -> handler.getEventClass().isAssignableFrom(payload.getClass()))
                    .collect(toList());

            for (DomainEventHandler<E> handler : suitableHandlers) {
                EventHandlerContext<E> context = EventHandlerContext.<E>builder()
                        .aggregateId(getAggregateId(headers))
                        .aggregateType(getAggregateType(headers))
                        .eventId(getEventId(headers))
                        .domainEvent(handler.getEventClass().cast(payload))
                        .timestamp(headers.getTimestamp())
                        .build();

                handler.handle(context);
            }
        } catch (Exception e) {
            log.error("Listener execution failed", e);

            throw new RuntimeException("Listener execution failed", e);
        }
    }
}
