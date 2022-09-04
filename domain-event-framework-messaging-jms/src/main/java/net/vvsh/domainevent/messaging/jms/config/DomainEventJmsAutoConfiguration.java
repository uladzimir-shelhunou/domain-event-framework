package net.vvsh.domainevent.messaging.jms.config;

import net.vvsh.domainevent.core.config.DomainEventProperties;
import net.vvsh.domainevent.core.domain.DomainEvent;
import net.vvsh.domainevent.core.handler.DomainEventHandlerRegistry;
import net.vvsh.domainevent.messaging.jms.config.properties.DomainEventJmsProperties;
import net.vvsh.domainevent.messaging.jms.producer.JmsMessageProducer;
import net.vvsh.domainevent.core.service.MessageProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.RedeliveryPolicy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQConnectionFactoryCustomizer;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import javax.jms.ConnectionFactory;

import static org.springframework.boot.autoconfigure.jms.JmsProperties.AcknowledgeMode.CLIENT;

@Configuration
@EnableJms
@PropertySource("classpath:config/domain-event-jms.properties")
@EnableConfigurationProperties({
    ActiveMQProperties.class,
    DomainEventJmsProperties.class
})
public class DomainEventJmsAutoConfiguration {

    @Bean
    public MessageProducer messageProducer(JmsMessagingTemplate jmsMessagingTemplate) {
        return new JmsMessageProducer(jmsMessagingTemplate);
    }

    @Bean
    public ActiveMQConnectionFactoryCustomizer configureRedeliveryPolicy(RedeliveryPolicy redeliveryPolicy) {
        return connectionFactory -> {
            connectionFactory.setRedeliveryPolicy(redeliveryPolicy);
        };
    }

    @Bean
    @ConditionalOnMissingBean(RedeliveryPolicy.class)
    public RedeliveryPolicy redeliveryPolicy(DomainEventJmsProperties domainEventJmsProperties) {
        DomainEventJmsProperties.RedeliveryPolicy policyProperties = domainEventJmsProperties.getRedeliveryPolicy();

        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(policyProperties.getInitialDelay());
        redeliveryPolicy.setBackOffMultiplier(policyProperties.getBackoffMultiplier());
        redeliveryPolicy.setUseExponentialBackOff(policyProperties.isUseExponentialBackoff());
        redeliveryPolicy.setMaximumRedeliveries(policyProperties.getMaxRedeliveries());
        return redeliveryPolicy;
    }

    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("message-type");
        converter.setObjectMapper(objectMapper);
        return converter;
    }

    @Bean
    @ConditionalOnMissingBean(JmsListenerContainerFactory.class)
    public JmsListenerContainerFactory<DefaultMessageListenerContainer> jmsListenerContainerFactory(
        ConnectionFactory connectionFactory,
        DefaultJmsListenerContainerFactoryConfigurer configurer) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setSessionAcknowledgeMode(CLIENT.getMode());
        return factory;
    }

    @Bean
    public <E extends DomainEvent> JmsDomainEventListenerConfigurer<E> jmsDomainEventListenerConfigurer(
        DomainEventHandlerRegistry<E> registry,
        JmsListenerContainerFactory<DefaultMessageListenerContainer> containerFactory,
        DomainEventProperties properties,
        MessageConverter messageConverter) {
        return new JmsDomainEventListenerConfigurer<>(registry, containerFactory, properties, messageConverter);
    }
}
