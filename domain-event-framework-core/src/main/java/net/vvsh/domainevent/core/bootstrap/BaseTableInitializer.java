package net.vvsh.domainevent.core.bootstrap;

import static org.springframework.util.StringUtils.hasText;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.lang.NonNull;

@Slf4j
@AllArgsConstructor
public abstract class BaseTableInitializer implements InitializingBean {

    private final JdbcTemplate jdbcTemplate;
    private final JpaProperties jpaProperties;

    @Override
    public void afterPropertiesSet() {
        String schemaName = jpaProperties.getProperties().get(AvailableSettings.DEFAULT_SCHEMA);
        if (!hasText(schemaName)) {
            throw new IllegalStateException("Please define service schema using `hibernate.default_schema` property");
        }

        jdbcTemplate.execute(createStatement(schemaName));
    }

    @NonNull
    protected abstract StatementCallback<Boolean> createStatement(String schemaName);

}
