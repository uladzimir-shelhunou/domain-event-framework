package net.vvsh.domainevent.messaging.kafka.config;

import net.vvsh.domainevent.core.config.DomainEventProperties;
import net.vvsh.domainevent.core.domain.DomainEvent;
import net.vvsh.domainevent.core.handler.DomainEventHandlerRegistry;
import net.vvsh.domainevent.messaging.kafka.config.properties.DomainEventKafkaProperties;
import net.vvsh.domainevent.messaging.kafka.producer.KafkaMessageProducer;
import net.vvsh.domainevent.core.service.MessageProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.mapping.DefaultJackson2JavaTypeMapper;
import org.springframework.kafka.support.mapping.Jackson2JavaTypeMapper;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.SmartMessageConverter;

import java.util.Map;
import java.util.UUID;

@Configuration
@EnableKafka
@PropertySources({
    @PropertySource("classpath:config/application-kafka.properties"),
    @PropertySource("classpath:config/domain-event-kafka.properties")
})
@EnableConfigurationProperties({
    KafkaProperties.class,
    DomainEventKafkaProperties.class
})
public class DomainEventKafkaAutoConfiguration {

    @Bean
    public MessageProducer messageProducer(KafkaTemplate<UUID, DomainEvent> kafkaTemplate) {
        return new KafkaMessageProducer(kafkaTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(JsonSerializer.class)
    public <E extends DomainEvent> JsonSerializer<E> kafkaJsonSerializer(ObjectMapper objectMapper,
                                                                         Jackson2JavaTypeMapper typeMapper) {
        JsonSerializer<E> jsonSerializer = new JsonSerializer<>(objectMapper);
        jsonSerializer.setTypeMapper(typeMapper);
        return jsonSerializer;
    }

    @Bean
    @ConditionalOnMissingBean(JsonDeserializer.class)
    public <E extends DomainEvent> JsonDeserializer<E> kafkaJsonDeserializer(ObjectMapper objectMapper,
                                                                             Jackson2JavaTypeMapper typeMapper) {
        JsonDeserializer<E> jsonDeserializer = new JsonDeserializer<>(objectMapper);
        jsonDeserializer.setTypeMapper(typeMapper);
        return jsonDeserializer;
    }

    @Bean
    @ConditionalOnMissingBean(Jackson2JavaTypeMapper.class)
    public Jackson2JavaTypeMapper jackson2JavaTypeMapper(DomainEventKafkaProperties properties) {
        DefaultJackson2JavaTypeMapper mapper = new DefaultJackson2JavaTypeMapper();
        mapper.addTrustedPackages(properties.getTrustedPackages().toArray(String[]::new));
        mapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        return mapper;
    }

    @Bean
    @ConditionalOnMissingBean(KafkaAdmin.class)
    public KafkaAdmin kafkaAdmin(KafkaProperties kafkaProperties) {
        Map<String, Object> adminProperties = kafkaProperties.buildAdminProperties();

        KafkaAdmin kafkaAdmin = new KafkaAdmin(adminProperties);
        kafkaAdmin.setFatalIfBrokerNotAvailable(true);
        return kafkaAdmin;
    }

    @Bean
    @ConditionalOnMissingBean(ConsumerFactory.class)
    public <E extends DomainEvent> ConsumerFactory<UUID, E> consumerFactory(KafkaProperties kafkaProperties,
                                                                            JsonDeserializer<E> jsonDeserializer) {
        Map<String, Object> consumerProperties = kafkaProperties.buildConsumerProperties();

        DefaultKafkaConsumerFactory<UUID, E> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProperties);
        consumerFactory.setKeyDeserializer(new UUIDDeserializer());
        consumerFactory.setValueDeserializer(jsonDeserializer);
        return consumerFactory;
    }

    @Bean
    @ConditionalOnMissingBean(ProducerFactory.class)
    public <E extends DomainEvent> ProducerFactory<UUID, E> producerFactory(KafkaProperties kafkaProperties,
                                                                            JsonSerializer<E> jsonSerializer) {
        Map<String, Object> producerProperties = kafkaProperties.buildProducerProperties();

        DefaultKafkaProducerFactory<UUID, E> producerFactory = new DefaultKafkaProducerFactory<>(producerProperties);
        producerFactory.setKeySerializer(new UUIDSerializer());
        producerFactory.setValueSerializer(jsonSerializer);
        return producerFactory;
    }

    @Bean
    @ConditionalOnMissingBean(ConcurrentKafkaListenerContainerFactory.class)
    public <E extends DomainEvent> ConcurrentKafkaListenerContainerFactory<UUID, E> containerFactory(
        ConsumerFactory<UUID, E> consumerFactory,
        DomainEventKafkaProperties properties) {
        ConcurrentKafkaListenerContainerFactory<UUID, E> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    @Bean
    public SmartMessageConverter messageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        return converter;
    }

    @Bean
    public <E extends DomainEvent> KafkaDomainEventListenerConfigurer<E> domainEventListenerConfigurer(
        DomainEventHandlerRegistry<E> registry,
        ConcurrentKafkaListenerContainerFactory<UUID, E> containerFactory,
        DomainEventProperties properties,
        SmartMessageConverter messageConverter,
        BeanFactory beanFactory,
        KafkaAdmin kafkaAdmin) {
        return new KafkaDomainEventListenerConfigurer<>(registry, containerFactory, properties,
                                                        messageConverter, beanFactory, kafkaAdmin);
    }
}
