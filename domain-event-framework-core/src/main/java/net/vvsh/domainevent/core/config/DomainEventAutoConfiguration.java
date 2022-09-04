package net.vvsh.domainevent.core.config;

import static java.util.stream.Collectors.toList;

import net.vvsh.domainevent.core.domain.DomainEvent;
import net.vvsh.domainevent.core.handler.DomainEventHandler;
import net.vvsh.domainevent.core.handler.DomainEventHandlerRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan({
    "net.vvsh.domainevent.core.config",
    "net.vvsh.domainevent.core.mapper",
})
@PropertySource("classpath:config/domain-event.properties")
@EnableConfigurationProperties(DomainEventProperties.class)
@AutoConfigureAfter(JacksonAutoConfiguration.class)
@EnableScheduling
@ConditionalOnProperty(prefix = "domain-event.core", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DomainEventAutoConfiguration {

    @Bean
    public <E extends DomainEvent> DomainEventHandlerRegistry<E> domainEventHandlerRegistry(
        ObjectProvider<DomainEventHandler<E>> provider) {
        return new DomainEventHandlerRegistry<>(provider.stream().collect(toList()));
    }
}
