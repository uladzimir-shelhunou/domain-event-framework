package net.vvsh.domainevent.messaging.jms.config;

import net.vvsh.domainevent.core.config.DomainEventProperties;
import net.vvsh.domainevent.core.domain.DomainEvent;
import net.vvsh.domainevent.core.handler.DomainEventHandler;
import net.vvsh.domainevent.core.handler.DomainEventHandlerRegistry;
import net.vvsh.domainevent.core.handler.HandlerType;
import net.vvsh.domainevent.messaging.jms.listener.JmsMessageListenerAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListenerConfigurer;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerEndpointRegistrar;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static net.vvsh.domainevent.messaging.jms.constant.JmsConstants.CONSUMER_DOMAIN_EVENT_QUEUE_TEMPLATE;

@RequiredArgsConstructor
public class JmsDomainEventListenerConfigurer<E extends DomainEvent> implements JmsListenerConfigurer {

    private final static AtomicInteger COUNTER = new AtomicInteger();

    private final DomainEventHandlerRegistry<E> registry;
    private final JmsListenerContainerFactory<DefaultMessageListenerContainer> containerFactory;
    private final DomainEventProperties properties;
    private final MessageConverter messageConverter;

    @Override
    public void configureJmsListeners(@NonNull JmsListenerEndpointRegistrar registrar) {
        Map<String, List<DomainEventHandler<E>>> allHandlers = registry.getHandlersByType(HandlerType.REMOTE);
        if (allHandlers.isEmpty()) {
            return;
        }

        String serviceId = properties.getServiceId();
        if (!StringUtils.hasText(serviceId)) {
            throw new IllegalStateException("`domain-event.core.service-id` property value is missing.");
        }

        allHandlers.forEach((aggregateType, handlers) -> {
            SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
            endpoint.setId(generateEndpointId());
            endpoint.setDestination(buildQueueName(serviceId, aggregateType));
            endpoint.setMessageListener(new JmsMessageListenerAdapter<>(handlers, messageConverter));
            registrar.registerEndpoint(endpoint, containerFactory);
        });
    }

    @NonNull
    private String buildQueueName(@NonNull String serviceId, @NonNull String aggregateType) {
        return CONSUMER_DOMAIN_EVENT_QUEUE_TEMPLATE.formatted(serviceId, aggregateType);
    }

    @NonNull
    private String generateEndpointId() {
        return SimpleJmsListenerEndpoint.class.getSimpleName() + "#" + COUNTER.getAndIncrement();
    }
}
