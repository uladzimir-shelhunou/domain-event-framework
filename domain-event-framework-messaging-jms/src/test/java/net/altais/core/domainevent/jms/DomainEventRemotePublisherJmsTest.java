package net.altais.core.domainevent.jms;

import net.altais.core.domainevent.config.DatabaseConfiguration;
import net.vvsh.domainevent.core.config.DomainEventAutoConfiguration;
import net.altais.core.domainevent.config.TestAppCommonConfiguration;
import net.vvsh.domainevent.core.domain.EventHandlerContext;
import net.altais.core.domainevent.domain.TestAggregateType;
import net.altais.core.domainevent.domain.TestDomainEvent;
import net.vvsh.domainevent.core.entity.OutboxEntity;
import net.vvsh.domainevent.messaging.jms.config.DomainEventJmsAutoConfiguration;
import net.altais.core.domainevent.jms.config.TestAppJMSConfiguration;
import net.vvsh.domainevent.core.repository.OutboxRepository;
import net.altais.core.domainevent.service.EventLogServiceProxy;
import net.altais.core.domainevent.service.TestDomainEventConsumer;
import net.altais.core.domainevent.service.TestDomainEventPublisher;
import net.altais.core.domainevent.service.TestRemoteDomainEventConsumer;
import net.vvsh.core.embeddeddb.config.EnableEmbeddedDatabase;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {
    TestAppCommonConfiguration.class,
    TestAppJMSConfiguration.class,
    DatabaseConfiguration.class
})
@ImportAutoConfiguration(classes = {
    JacksonAutoConfiguration.class,
    JmsAutoConfiguration.class,
    LiquibaseAutoConfiguration.class,
    DomainEventJmsAutoConfiguration.class,
    DomainEventAutoConfiguration.class
})
@EnableEmbeddedDatabase(refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_CLASS)
@ActiveProfiles({"embedded", "embedded-jms"})
class DomainEventRemotePublisherJmsTest {

    private static final UUID AGGREGATE_ID = UUID.randomUUID();

    @Autowired
    private TestRemoteDomainEventConsumer testRemoteDomainEventConsumer;

    @Autowired
    private TestDomainEventConsumer testDomainEventConsumer;

    @Autowired
    private TestDomainEventPublisher testDomainEventPublisher;

    @Autowired
    private EventLogServiceProxy eventLogServiceProxy;

    @Autowired
    private OutboxRepository outboxRepository;

    @AfterEach
    void clear() {
        testRemoteDomainEventConsumer.clear();
        testDomainEventConsumer.clear();
    }

    @Test
    void shouldPublish() throws InterruptedException {
        TestDomainEvent domainEvent = new TestDomainEvent();

        testDomainEventPublisher.publish(TestAggregateType.TEST_AGGREGATE, AGGREGATE_ID, domainEvent);

        Thread.sleep(5000);

        List<OutboxEntity> allEntities = outboxRepository.findAll();
        assertEquals(1, allEntities.size());

        List<EventHandlerContext<TestDomainEvent>> contexts = testRemoteDomainEventConsumer.getReceivedEventContexts();
        assertEquals(1, contexts.size());

        EventHandlerContext<TestDomainEvent> context = contexts.get(0);
        assertEquals(TestAggregateType.TEST_AGGREGATE.getName(), context.getAggregateType());
        assertEquals(domainEvent.getEventType(), context.getDomainEventType());
        assertEquals(AGGREGATE_ID, context.getAggregateId());
        assertEquals(domainEvent, context.getDomainEvent());
        assertEquals(allEntities.get(0).getEventId(), context.getEventId());

        assertTrue(eventLogServiceProxy.isEventProcessed(TestAggregateType.TEST_AGGREGATE, context.getEventId()));
    }
}