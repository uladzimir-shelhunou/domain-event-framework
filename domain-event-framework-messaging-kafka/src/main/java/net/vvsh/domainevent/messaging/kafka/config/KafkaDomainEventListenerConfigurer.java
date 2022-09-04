package net.vvsh.domainevent.messaging.kafka.config;

import net.vvsh.domainevent.core.config.DomainEventProperties;
import net.vvsh.domainevent.core.domain.DomainEvent;
import net.vvsh.domainevent.core.handler.DomainEventHandler;
import net.vvsh.domainevent.core.handler.DomainEventHandlerRegistry;
import net.vvsh.domainevent.core.handler.HandlerType;
import net.vvsh.domainevent.messaging.kafka.listener.KafkaMessageListenerAdapter;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.lang.NonNull;
import org.springframework.messaging.converter.SmartMessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static net.vvsh.domainevent.messaging.kafka.constant.KafkaConstants.DEFAULT_PARTITION_COUNT;
import static net.vvsh.domainevent.messaging.kafka.constant.KafkaConstants.DEFAULT_REPLICATION_FACTOR;
import static net.vvsh.domainevent.messaging.kafka.constant.KafkaConstants.DOMAIN_EVENT_TOPIC_TEMPLATE;
import static org.springframework.util.ReflectionUtils.findMethod;

@RequiredArgsConstructor
public class KafkaDomainEventListenerConfigurer<E extends DomainEvent> implements KafkaListenerConfigurer {

    private final static AtomicInteger COUNTER = new AtomicInteger();

    private final DomainEventHandlerRegistry<E> registry;
    private final ConcurrentKafkaListenerContainerFactory<UUID, E> containerFactory;
    private final DomainEventProperties properties;
    private final SmartMessageConverter messageConverter;
    private final BeanFactory beanFactory;
    private final KafkaAdmin kafkaAdmin;

    @Override
    public void configureKafkaListeners(@NonNull KafkaListenerEndpointRegistrar registrar) {
        Map<String, List<DomainEventHandler<E>>> allHandlers = registry.getHandlersByType(HandlerType.REMOTE);
        if (allHandlers.isEmpty()) {
            return;
        }

        String serviceId = properties.getServiceId();
        if (!StringUtils.hasText(serviceId)) {
            throw new IllegalStateException("`domain-event.core.service-id` property value is missing.");
        }

        allHandlers.forEach((aggregateType, handlers) -> {
            KafkaMessageListenerAdapter<UUID, E> messageListenerAdapter = new KafkaMessageListenerAdapter<>(
                handlers);
            registerHandlerForAggregateType(aggregateType, serviceId, messageListenerAdapter, registrar);
        });
    }

    private void registerHandlerForAggregateType(String aggregateType,
                                                 String serviceId,
                                                 KafkaMessageListenerAdapter<UUID, E> messageListenerAdapter,
                                                 KafkaListenerEndpointRegistrar registrar) {
        String topicName = buildTopicName(aggregateType);
        createTopicIfNecessary(topicName);

        MethodKafkaListenerEndpoint<UUID, E> endpoint = new MethodKafkaListenerEndpoint<>();
        endpoint.setId(generateEndpointId());
        endpoint.setGroupId(serviceId);
        endpoint.setTopics(topicName);
        endpoint.setBean(messageListenerAdapter);
        endpoint.setMethod(getMethod(messageListenerAdapter));
        endpoint.setBeanFactory(beanFactory);
        endpoint.setMessagingConverter(messageConverter);
        endpoint.setMessageHandlerMethodFactory(getMessageHandlerMethodFactory());
        endpoint.setAutoStartup(true);
        registrar.registerEndpoint(endpoint, containerFactory);
    }

    private void createTopicIfNecessary(String topicName) {
        NewTopic newTopic = new NewTopic(topicName, DEFAULT_PARTITION_COUNT, DEFAULT_REPLICATION_FACTOR);
        kafkaAdmin.createOrModifyTopics(newTopic);
    }

    private DefaultMessageHandlerMethodFactory getMessageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setBeanFactory(beanFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }

    private Method getMethod(AcknowledgingMessageListener<UUID, E> listener) {
        return findMethod(listener.getClass(), "onMessage", ConsumerRecord.class, Acknowledgment.class);
    }

    @NonNull
    private String buildTopicName(@NonNull String aggregateType) {
        return DOMAIN_EVENT_TOPIC_TEMPLATE.formatted(aggregateType);
    }

    @NonNull
    private String generateEndpointId() {
        return MethodKafkaListenerEndpoint.class.getSimpleName() + "#" + COUNTER.getAndIncrement();
    }
}
