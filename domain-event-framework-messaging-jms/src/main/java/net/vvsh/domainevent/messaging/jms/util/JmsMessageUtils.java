package net.vvsh.domainevent.messaging.jms.util;

import net.vvsh.domainevent.messaging.jms.constant.JmsConstants;
import lombok.experimental.UtilityClass;
import org.springframework.lang.Nullable;
import org.springframework.messaging.MessageHeaders;

import java.util.UUID;

@UtilityClass
public class JmsMessageUtils {

    @Nullable
    public static String getAggregateType(MessageHeaders headers) {
        return headers.get(JmsConstants.HEADER_EVENT_AGGREGATE_TYPE, String.class);
    }

    @Nullable
    public static UUID getAggregateId(MessageHeaders headers) {
        return getId(headers, JmsConstants.HEADER_EVENT_AGGREGATE_ID);
    }

    @Nullable
    public static UUID getEventId(MessageHeaders headers) {
        return getId(headers, JmsConstants.HEADER_EVENT_ID);
    }

    @Nullable
    private static UUID getId(MessageHeaders headers, String headerName) {
        String value = headers.get(headerName, String.class);
        return value == null ? null : UUID.fromString(value);
    }
}
