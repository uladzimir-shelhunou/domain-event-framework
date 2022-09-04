package net.vvsh.domainevent.messaging.jms.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class JmsConstants {

    public static final String DOMAIN_EVENT_TOPIC_TEMPLATE = "VirtualTopic.DomainEvent.%s";
    public static final String CONSUMER_DOMAIN_EVENT_QUEUE_TEMPLATE = "Consumer.%s.VirtualTopic.DomainEvent.%s";

    public static final String HEADER_EVENT_AGGREGATE_TYPE = "event-aggregate-type";
    public static final String HEADER_EVENT_AGGREGATE_ID = "event-aggregate-id";
    public static final String HEADER_EVENT_ID = "event-id";
    public static final String HEADER_EVENT_TYPE = "event-type";
}
