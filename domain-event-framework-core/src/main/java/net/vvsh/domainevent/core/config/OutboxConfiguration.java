package net.vvsh.domainevent.core.config;

import net.vvsh.domainevent.core.bootstrap.OutboxTableInitializer;
import net.vvsh.domainevent.core.config.condition.OutboxEnabledCondition;
import net.vvsh.domainevent.core.domain.DomainEvent;
import net.vvsh.domainevent.core.handler.DomainEventHandlerRegistry;
import net.vvsh.domainevent.core.mapper.DomainEventMapper;
import net.vvsh.domainevent.core.repository.OutboxRepository;
import net.vvsh.domainevent.core.service.DomainEventConsumer;
import net.vvsh.domainevent.core.service.DomainEventPublisher;
import net.vvsh.domainevent.core.service.DomainEventService;
import net.vvsh.domainevent.core.service.MessageProducer;
import net.vvsh.domainevent.core.service.MessageRelayService;
import net.vvsh.domainevent.core.service.local.LocalDomainEventProcessor;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@Conditional(OutboxEnabledCondition.class)
public class OutboxConfiguration {

    @Bean
    public DomainEventService domainEventService(OutboxRepository outboxRepository, DomainEventMapper domainEventMapper) {
        return new DomainEventService(outboxRepository, domainEventMapper);
    }

    @Bean
    public DomainEventPublisher domainEventPublisher(ApplicationEventPublisher eventPublisher) {
        return new DomainEventPublisher(eventPublisher);
    }

    @Bean
    public DomainEventConsumer domainEventConsumer(DomainEventService domainEventService) {
        return new DomainEventConsumer(domainEventService);
    }

    @Bean
    public <E extends DomainEvent> LocalDomainEventProcessor<E> localDomainEventProcessor(
        DomainEventHandlerRegistry<E> registry) {
        return new LocalDomainEventProcessor<>(registry);
    }

    @Bean
    public MessageRelayService messageRelayService(OutboxRepository outboxRepository,
                                                   MessageProducer messageProducer,
                                                   DomainEventProperties domainEventProperties) {
        return new MessageRelayService(outboxRepository, messageProducer, domainEventProperties);
    }

    @Bean
    public OutboxTableInitializer outboxTableInitializer(JdbcTemplate jdbcTemplate, JpaProperties jpaProperties) {
        return new OutboxTableInitializer(jdbcTemplate, jpaProperties);
    }

}
