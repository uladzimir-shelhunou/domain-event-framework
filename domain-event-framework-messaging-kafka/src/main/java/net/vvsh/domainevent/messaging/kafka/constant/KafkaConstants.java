package net.vvsh.domainevent.messaging.kafka.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KafkaConstants {

    public static final String DOMAIN_EVENT_TOPIC_TEMPLATE = "DomainEvent.%s";
    public static final int DEFAULT_PARTITION_COUNT = 32;
    public static final short DEFAULT_REPLICATION_FACTOR = 1;

    public static final String HEADER_EVENT_AGGREGATE_TYPE = "event-aggregate-type";
    public static final String HEADER_EVENT_AGGREGATE_ID = "event-aggregate-id";
    public static final String HEADER_EVENT_ID = "event-id";
    public static final String HEADER_EVENT_TYPE = "event-type";

}
