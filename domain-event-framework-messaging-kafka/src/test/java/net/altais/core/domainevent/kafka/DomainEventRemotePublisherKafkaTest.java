package net.altais.core.domainevent.kafka;

import net.altais.core.domainevent.config.DatabaseConfiguration;
import net.vvsh.domainevent.core.config.DomainEventAutoConfiguration;
import net.altais.core.domainevent.config.TestAppCommonConfiguration;
import net.vvsh.domainevent.core.domain.EventHandlerContext;
import net.altais.core.domainevent.domain.TestAggregateType;
import net.altais.core.domainevent.domain.TestDomainEvent;
import net.vvsh.domainevent.core.entity.OutboxEntity;
import net.vvsh.domainevent.messaging.kafka.config.DomainEventKafkaAutoConfiguration;
import net.altais.core.domainevent.kafka.config.TestAppKafkaConfiguration;
import net.vvsh.domainevent.core.repository.OutboxRepository;
import net.altais.core.domainevent.service.EventLogServiceProxy;
import net.altais.core.domainevent.service.TestDomainEventPublisher;
import net.altais.core.domainevent.service.TestRemoteDomainEventConsumer;
import net.vvsh.core.embeddeddb.config.EnableEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {
    TestAppCommonConfiguration.class,
    TestAppKafkaConfiguration.class,
    DatabaseConfiguration.class
})
@DirtiesContext
@ImportAutoConfiguration(classes = {
    JacksonAutoConfiguration.class,
    KafkaAutoConfiguration.class,
    LiquibaseAutoConfiguration.class,
    DomainEventAutoConfiguration.class,
    DomainEventKafkaAutoConfiguration.class
})
@EnableEmbeddedDatabase(refresh = RefreshMode.AFTER_CLASS)
@EmbeddedKafka(count = 1)
@ActiveProfiles({"embedded", "embedded-kafka"})
class DomainEventRemotePublisherKafkaTest {

    private static final UUID AGGREGATE_ID = UUID.randomUUID();

    @Autowired
    private TestRemoteDomainEventConsumer testRemoteDomainEventConsumer;

    @Autowired
    private TestDomainEventPublisher testDomainEventPublisher;

    @Autowired
    private EventLogServiceProxy eventLogServiceProxy;

    @Autowired
    private OutboxRepository outboxRepository;

    @Test
    void shouldPublish() throws InterruptedException {
        TestDomainEvent domainEvent = new TestDomainEvent();

        testDomainEventPublisher.publish(TestAggregateType.TEST_AGGREGATE, AGGREGATE_ID, domainEvent);

        Thread.sleep(10000);

        List<OutboxEntity> allEntities = outboxRepository.findAll();
        assertEquals(1, allEntities.size());

        List<EventHandlerContext<TestDomainEvent>> contexts = testRemoteDomainEventConsumer.getReceivedEventContexts();
        assertEquals(1, contexts.size());

        EventHandlerContext<TestDomainEvent> context = contexts.get(0);
        assertEquals(TestAggregateType.TEST_AGGREGATE.getName(), context.getAggregateType());
        Assertions.assertEquals(domainEvent.getEventType(), context.getDomainEventType());
        assertEquals(AGGREGATE_ID, context.getAggregateId());
        assertEquals(domainEvent, context.getDomainEvent());
        assertEquals(allEntities.get(0).getEventId(), context.getEventId());

        assertTrue(eventLogServiceProxy.isEventProcessed(TestAggregateType.TEST_AGGREGATE, context.getEventId()));
    }
}