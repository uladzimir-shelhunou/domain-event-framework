package net.vvsh.domainevent.core.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.lang.NonNull;

@Slf4j
public class EventLogTableInitializer extends BaseTableInitializer {

    public EventLogTableInitializer(JdbcTemplate jdbcTemplate, JpaProperties jpaProperties) {
        super(jdbcTemplate, jpaProperties);
    }

    @NonNull
    @Override
    protected StatementCallback<Boolean> createStatement(String schemaName) {
        return stmt -> {
            boolean result = stmt.execute("""
                                              CREATE TABLE IF NOT EXISTS %s.event_log (
                                                  aggregate_type varchar NOT NULL,
                                                  event_id uuid NOT NULL,
                                                  processed_date timestamptz NOT NULL,
                                                  CONSTRAINT inbox_pkey PRIMARY KEY (aggregate_type, event_id)
                                              );
                                              """.formatted(schemaName));

            if (result) {
                log.info("The message_log table created successfully.");
                return true;
            }

            log.info("The message_log table already exists.");
            return false;
        };
    }
}