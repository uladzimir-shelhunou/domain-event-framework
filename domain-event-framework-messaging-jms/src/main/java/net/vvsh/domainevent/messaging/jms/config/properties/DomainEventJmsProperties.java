package net.vvsh.domainevent.messaging.jms.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "domain-event.core.jms")
public class DomainEventJmsProperties {

    private RedeliveryPolicy redeliveryPolicy;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedeliveryPolicy {

        private int initialDelay;
        private boolean useExponentialBackoff;
        private double backoffMultiplier;
        private int maxRedeliveries;

    }
}
