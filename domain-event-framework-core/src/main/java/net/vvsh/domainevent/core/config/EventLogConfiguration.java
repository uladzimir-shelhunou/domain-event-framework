package net.vvsh.domainevent.core.config;

import net.vvsh.domainevent.core.bootstrap.EventLogTableInitializer;
import net.vvsh.domainevent.core.config.condition.EventLogEnabledCondition;
import net.vvsh.domainevent.core.repository.EventLogRepository;
import net.vvsh.domainevent.core.service.EventLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@RequiredArgsConstructor
@Conditional(EventLogEnabledCondition.class)
public class EventLogConfiguration {

    @Bean
    public EventLogService eventLogService(EventLogRepository eventLogRepository) {
        return new EventLogService(eventLogRepository);
    }

    @Bean
    public EventLogTableInitializer eventLogTableInitializer(JdbcTemplate jdbcTemplate, JpaProperties jpaProperties) {
        return new EventLogTableInitializer(jdbcTemplate, jpaProperties);
    }

}
