package net.vvsh.domainevent.messaging.kafka.util;

import lombok.experimental.UtilityClass;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.lang.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static net.vvsh.domainevent.messaging.kafka.constant.KafkaConstants.HEADER_EVENT_AGGREGATE_ID;
import static net.vvsh.domainevent.messaging.kafka.constant.KafkaConstants.HEADER_EVENT_AGGREGATE_TYPE;
import static net.vvsh.domainevent.messaging.kafka.constant.KafkaConstants.HEADER_EVENT_ID;

@UtilityClass
public class KafkaMessageUtils {

    @Nullable
    public static String getAggregateType(Headers headers) {
        Header header = headers.lastHeader(HEADER_EVENT_AGGREGATE_TYPE);
        return header == null ? null : new String(header.value());
    }

    @Nullable
    public static UUID getAggregateId(Headers headers) {
        return getId(headers, HEADER_EVENT_AGGREGATE_ID);
    }

    @Nullable
    public static UUID getEventId(Headers headers) {
        return getId(headers, HEADER_EVENT_ID);
    }

    @Nullable
    private static UUID getId(Headers headers, String headerName) {
        Header header = headers.lastHeader(headerName);
        String value = header != null ? new String(header.value(), StandardCharsets.UTF_8) : null;
        return value == null ? null : UUID.fromString(value);
    }
}
