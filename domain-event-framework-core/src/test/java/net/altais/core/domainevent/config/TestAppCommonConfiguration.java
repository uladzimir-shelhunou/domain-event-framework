package net.altais.core.domainevent.config;

import net.vvsh.domainevent.core.service.DomainEventPublisher;
import net.vvsh.domainevent.core.service.EventLogService;
import net.altais.core.domainevent.service.EventLogServiceProxy;
import net.altais.core.domainevent.service.TestDomainEventConsumer;
import net.altais.core.domainevent.service.TestDomainEventPublisher;
import net.altais.core.domainevent.service.TestRemoteDomainEventConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@Configuration
public class TestAppCommonConfiguration {

    @Bean
    public TestDomainEventConsumer testDomainEventConsumer() {
        return new TestDomainEventConsumer();
    }

    @Bean
    @Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
    public TestRemoteDomainEventConsumer testRemoteDomainEventConsumer() {
        return new TestRemoteDomainEventConsumer();
    }

    @Bean
    public EventLogServiceProxy eventLogServiceProxy(EventLogService eventLogService) {
        return new EventLogServiceProxy(eventLogService);
    }

    @Bean
    public TestDomainEventPublisher testDomainEventPublisher(DomainEventPublisher domainEventPublisher) {
        return new TestDomainEventPublisher(domainEventPublisher);
    }

}
