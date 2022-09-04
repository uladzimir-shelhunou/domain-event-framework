package net.vvsh.domainevent.core.service;

import net.vvsh.domainevent.core.entity.OutboxEntity;
import org.springframework.lang.NonNull;

public interface MessageProducer {

    /**
     * Send event to a message broker.
     *
     * @param outboxEntity
     *      event object
     */
    void send(@NonNull OutboxEntity outboxEntity);

}
