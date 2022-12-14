package net.altais.core.domainevent.service;

import net.vvsh.domainevent.core.domain.AggregateType;
import net.vvsh.domainevent.core.domain.EventHandlerContext;
import net.altais.core.domainevent.domain.TestAggregateType;
import net.altais.core.domainevent.domain.TestDomainEvent;
import net.vvsh.domainevent.core.handler.LocalDomainEventHandler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TestDomainEventConsumer implements LocalDomainEventHandler<TestDomainEvent> {

    private final List<EventHandlerContext<TestDomainEvent>> receivedEventContexts = new ArrayList<>();

    @Override
    public void handle(@NonNull EventHandlerContext<TestDomainEvent> context) {
        receivedEventContexts.add(context);
    }

    @NonNull
    @Override
    public AggregateType getAggregateType() {
        return TestAggregateType.TEST_AGGREGATE;
    }

    @NonNull
    @Override
    public Class<TestDomainEvent> getEventClass() {
        return TestDomainEvent.class;
    }

    public List<EventHandlerContext<TestDomainEvent>> getReceivedEventContexts() {
        return receivedEventContexts;
    }

    public void clear() {
        receivedEventContexts.clear();
    }
}
