package net.vvsh.domainevent.messaging.kafka.config.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "domain-event.core.kafka")
public class DomainEventKafkaProperties {

    private List<String> trustedPackages;

}
