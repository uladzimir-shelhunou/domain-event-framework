package net.altais.core.domainevent.kafka.config;

import net.vvsh.domainevent.core.config.DomainEventProperties;
import net.vvsh.domainevent.core.domain.DomainEvent;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.UUIDDeserializer;
import org.apache.kafka.common.serialization.UUIDSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

import java.util.Map;
import java.util.UUID;

@Configuration
@EnableKafka
public class TestAppKafkaConfiguration {

    @Bean
    public KafkaAdmin kafkaAdmin(EmbeddedKafkaBroker embeddedKafkaBroker, KafkaProperties kafkaProperties) {
        Map<String, Object> adminProperties = kafkaProperties.buildAdminProperties();
        adminProperties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString());

        return new KafkaAdmin(adminProperties);
    }

    @Bean
    public <E extends DomainEvent> ConsumerFactory<UUID, E> consumerFactory(EmbeddedKafkaBroker embeddedKafkaBroker,
                                                                            KafkaProperties kafkaProperties,
                                                                            DomainEventProperties domainEventProperties,
                                                                            JsonDeserializer<E> jsonDeserializer) {

        Map<String, Object> consumerProperties = kafkaProperties.buildConsumerProperties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString());
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, domainEventProperties.getServiceId());

        DefaultKafkaConsumerFactory<UUID, E> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProperties);
        consumerFactory.setKeyDeserializer(new UUIDDeserializer());
        consumerFactory.setValueDeserializer(jsonDeserializer);
        return consumerFactory;
    }

    @Bean
    public <E extends DomainEvent> ProducerFactory<UUID, E> producerFactory(EmbeddedKafkaBroker embeddedKafkaBroker,
                                                                            KafkaProperties kafkaProperties,
                                                                            JsonSerializer<E> jsonSerializer) {
        Map<String, Object> producerProperties = kafkaProperties.buildProducerProperties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafkaBroker.getBrokersAsString());

        DefaultKafkaProducerFactory<UUID, E> producerFactory = new DefaultKafkaProducerFactory<>(producerProperties);
        producerFactory.setKeySerializer(new UUIDSerializer());
        producerFactory.setValueSerializer(jsonSerializer);
        return producerFactory;
    }
}
