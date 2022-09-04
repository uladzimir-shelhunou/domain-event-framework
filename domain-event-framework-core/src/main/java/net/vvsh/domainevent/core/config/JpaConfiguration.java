package net.vvsh.domainevent.core.config;

import net.vvsh.domainevent.core.config.condition.PersistenceEnabledCondition;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "net.vvsh.domainevent.core.repository")
@Conditional(PersistenceEnabledCondition.class)
public class JpaConfiguration {
}
