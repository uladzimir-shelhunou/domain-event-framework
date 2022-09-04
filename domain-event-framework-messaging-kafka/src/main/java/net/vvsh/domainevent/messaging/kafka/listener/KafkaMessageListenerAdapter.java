package net.vvsh.domainevent.messaging.kafka.listener;

import net.vvsh.domainevent.core.domain.DomainEvent;
import net.vvsh.domainevent.core.domain.EventHandlerContext;
import net.vvsh.domainevent.core.handler.DomainEventHandler;
import net.vvsh.domainevent.messaging.kafka.util.KafkaMessageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
public class KafkaMessageListenerAdapter<K, V extends DomainEvent> implements AcknowledgingMessageListener<K, V> {

    private final List<DomainEventHandler<V>> handlers;

    @Override
    public void onMessage(ConsumerRecord<K, V> consumerRecord, Acknowledgment acknowledgment) {
        V domainEvent = consumerRecord.value();
        Headers headers = consumerRecord.headers();

        List<DomainEventHandler<V>> suitableHandlers = handlers.stream()
            .filter(handler -> handler.getEventClass().isAssignableFrom(domainEvent.getClass()))
            .toList();

        boolean processed = false;
        try {
            suitableHandlers.forEach(handler -> {
                EventHandlerContext<V> context = EventHandlerContext.<V>builder()
                    .aggregateId(KafkaMessageUtils.getAggregateId(headers))
                    .aggregateType(KafkaMessageUtils.getAggregateType(headers))
                    .eventId(KafkaMessageUtils.getEventId(headers))
                    .domainEvent(handler.getEventClass().cast(domainEvent))
                    .timestamp(consumerRecord.timestamp())
                    .build();

                handler.handle(context);
            });

            processed = true;
        } catch (Exception e) {
            log.error("Listener execution failed", e);

            throw new RuntimeException("Listener execution failed", e);
        } finally {
            if (processed) {
                acknowledgment.acknowledge();
            }
        }
    }
}
