package net.vvsh.domainevent.core.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "domain-event.core")
public class DomainEventProperties {

    private EventLogProperties eventLog;
    private OutboxProperties outbox;
    private Boolean enabled;
    private String serviceId;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventLogProperties {

        private Boolean enabled;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OutboxProperties {

        private Boolean enabled;
        private MessageRelayProperties messageRelay;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageRelayProperties {

        private Integer batchSize = 32;
    }
}
