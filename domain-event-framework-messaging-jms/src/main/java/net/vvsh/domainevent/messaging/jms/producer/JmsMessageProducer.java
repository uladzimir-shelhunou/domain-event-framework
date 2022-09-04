package net.vvsh.domainevent.messaging.jms.producer;

import net.vvsh.domainevent.core.entity.OutboxEntity;
import net.vvsh.domainevent.core.service.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.lang.NonNull;

import java.util.Map;

import static net.vvsh.domainevent.messaging.jms.constant.JmsConstants.DOMAIN_EVENT_TOPIC_TEMPLATE;
import static net.vvsh.domainevent.messaging.jms.constant.JmsConstants.HEADER_EVENT_AGGREGATE_ID;
import static net.vvsh.domainevent.messaging.jms.constant.JmsConstants.HEADER_EVENT_AGGREGATE_TYPE;
import static net.vvsh.domainevent.messaging.jms.constant.JmsConstants.HEADER_EVENT_ID;
import static net.vvsh.domainevent.messaging.jms.constant.JmsConstants.HEADER_EVENT_TYPE;

@RequiredArgsConstructor
public class JmsMessageProducer implements MessageProducer {

    private final JmsMessagingTemplate jmsMessagingTemplate;

    @Override
    public void send(@NonNull OutboxEntity outboxEntity) {
        String topic = buildTopicName(outboxEntity.getAggregateType());
        jmsMessagingTemplate.convertAndSend(
            new ActiveMQTopic(topic),
            outboxEntity.getPayload(),
            buildHeaders(outboxEntity)
        );
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
