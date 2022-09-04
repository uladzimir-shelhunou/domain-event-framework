package net.altais.core.domainevent.service.local;

import net.altais.core.domainevent.config.DatabaseConfiguration;
import net.vvsh.domainevent.core.config.DomainEventAutoConfiguration;
import net.altais.core.domainevent.config.TestAppCommonConfiguration;
import net.vvsh.domainevent.core.domain.EventHandlerContext;
import net.altais.core.domainevent.domain.TestAggregateType;
import net.altais.core.domainevent.domain.TestDomainEvent;
import net.vvsh.domainevent.core.service.DomainEventConsumer;
import net.vvsh.domainevent.core.service.MessageProducer;
import net.altais.core.domainevent.service.TestDomainEventConsumer;
import net.altais.core.domainevent.service.TestDomainEventPublisher;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {
    TestAppCommonConfiguration.class
})
@MockBean({
    DomainEventConsumer.class,
    MessageProducer.class
})
@ImportAutoConfiguration(classes = {
    JacksonAutoConfiguration.class, JmsAutoConfiguration.class,
    LiquibaseAutoConfiguration.class, DatabaseConfiguration.class,
    DomainEventAutoConfiguration.class
})
@EnableEmbeddedDatabase(refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_CLASS)
@ActiveProfiles("embedded")
class DomainEventLocalPublisherTest {

    private static final UUID AGGREGATE_ID = UUID.randomUUID();

    @Autowired
    private TestDomainEventPublisher domainEventPublisher;

    @Autowired
    private TestDomainEventConsumer testDomainEventConsumer;

    @AfterEach
    void clear() {
        testDomainEventConsumer.clear();
    }

    @Test
    void shouldPublish() {
        TestDomainEvent domainEvent = new TestDomainEvent();

        domainEventPublisher.publish(
            TestAggregateType.TEST_AGGREGATE,
            AGGREGATE_ID,
            domainEvent
        );

        List<EventHandlerContext<TestDomainEvent>> contexts = testDomainEventConsumer.getReceivedEventContexts();
        assertEquals(1, contexts.size());
    }

    @Test
    void shouldPublishBulk() {
        TestDomainEvent domainEvent = new TestDomainEvent();

        domainEventPublisher.publish(
            TestAggregateType.TEST_AGGREGATE,
            AGGREGATE_ID,
            List.of(domainEvent, domainEvent)
        );

        List<EventHandlerContext<TestDomainEvent>> contexts = testDomainEventConsumer.getReceivedEventContexts();
        assertEquals(2, contexts.size());
    }
}