package net.vvsh.domainevent.messaging.kafka.producer;

import net.vvsh.domainevent.core.domain.DomainEvent;
import net.vvsh.domainevent.core.entity.OutboxEntity;
import net.vvsh.domainevent.core.service.MessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.lang.NonNull;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.springframework.lang.Nullable;

import static net.vvsh.domainevent.messaging.kafka.constant.KafkaConstants.DOMAIN_EVENT_TOPIC_TEMPLATE;
import static net.vvsh.domainevent.messaging.kafka.constant.KafkaConstants.HEADER_EVENT_AGGREGATE_ID;
import static net.vvsh.domainevent.messaging.kafka.constant.KafkaConstants.HEADER_EVENT_AGGREGATE_TYPE;
import static net.vvsh.domainevent.messaging.kafka.constant.KafkaConstants.HEADER_EVENT_ID;
import static net.vvsh.domainevent.messaging.kafka.constant.KafkaConstants.HEADER_EVENT_TYPE;

@Slf4j
@RequiredArgsConstructor
public class KafkaMessageProducer implements MessageProducer {

    private static final Integer DEFAULT_TIMEOUT = 10;
    private final KafkaTemplate<UUID, DomainEvent> kafkaTemplate;

    @Override
    public void send(@NonNull OutboxEntity outboxEntity) {
        String topic = buildTopicName(outboxEntity.getAggregateType());

        var record = new ProducerRecord<>(topic,
                                          outboxEntity.getAggregateId(),
                                          outboxEntity.getPayload());

        buildHeaders(outboxEntity)
            .forEach((key, value) -> record.headers().add(key, buildHeaderValue(value)));

        try {
            kafkaTemplate.send(record).get(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("Unable to send message to a message broker", e);
        }
    }

    @Nullable
    private byte[] buildHeaderValue(Object value) {
        if (value == null) {
            return null;
        }

        return value.toString().getBytes(StandardCharsets.UTF_8);
    }

    @NonNull
    private String buildTopicName(String aggregateType) {
        return DOMAIN_EVENT_TOPIC_TEMPLATE.formatted(aggregateType);
    }

    @NonNull
    private Map<String, Object> buildHeaders(@NonNull OutboxEntity outboxEntity) {
        return Map.of(
            HEADER_EVENT_AGGREGATE_TYPE, outboxEntity.getAggregateType(),
            HEADER_EVENT_AGGREGATE_ID, outboxEntity.getAggregateId().toString(),
            HEADER_EVENT_TYPE, outboxEntity.getEventType(),
            HEADER_EVENT_ID, outboxEntity.getEventId().toString()
        );
    }
}
