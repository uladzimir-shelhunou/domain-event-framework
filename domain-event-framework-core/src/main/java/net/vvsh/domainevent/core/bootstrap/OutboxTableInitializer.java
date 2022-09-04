package net.vvsh.domainevent.core.bootstrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.lang.NonNull;

@Slf4j
public class OutboxTableInitializer extends BaseTableInitializer {

    public OutboxTableInitializer(JdbcTemplate jdbcTemplate, JpaProperties jpaProperties) {
        super(jdbcTemplate, jpaProperties);
    }

    @Override
    @NonNull
    protected StatementCallback<Boolean> createStatement(String schemaName) {
        return stmt -> {
            boolean result = stmt.execute("""
                    CREATE TABLE IF NOT EXISTS %s.outbox (
                        event_id uuid NOT NULL,
                        event_type varchar NOT NULL,
                        aggregate_type varchar NOT NULL,
                        aggregate_id uuid NOT NULL,
                        payload text,
                        submitted_date timestamptz NOT NULL DEFAULT now(),
                        fulfillment_date timestamptz,
                        CONSTRAINT outbox_pkey PRIMARY KEY (event_id)
                    );
                    """.formatted(schemaName));

            if (result) {
                log.info("The outbox table created successfully.");
                return true;
            }
            log.info("The outbox table already exists.");
            return false;
        };
    }

}