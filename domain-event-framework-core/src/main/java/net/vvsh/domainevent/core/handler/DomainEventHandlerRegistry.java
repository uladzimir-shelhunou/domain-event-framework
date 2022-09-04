package net.vvsh.domainevent.core.handler;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import net.vvsh.domainevent.core.domain.DomainEvent;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Slf4j
public class DomainEventHandlerRegistry<E extends DomainEvent> {

    private final Map<HandlerType, Map<String, List<DomainEventHandler<E>>>> registry;

    public DomainEventHandlerRegistry(List<DomainEventHandler<E>> handlers) {
        validate(handlers);

        registry = handlers.stream()
            .collect(
                groupingBy(DomainEventHandler::getHandlerType,
                           groupingBy(handler -> handler.getAggregateType().getName()))
            );
    }

    private void validate(List<DomainEventHandler<E>> handlers) {
        handlers.forEach(handler -> {
            log.debug("Validating handler of type: {}", handler.getClass());
            Objects.requireNonNull(handler.getAggregateType(), "Aggregate type must not be null");
            Objects.requireNonNull(handler.getEventClass(), "Event class must not be null");
            Objects.requireNonNull(handler.getHandlerType(), "Handler Type must not be null");
        });
    }

    @NonNull
    public Map<String, List<DomainEventHandler<E>>> getHandlersByType(@NonNull
                                                                      HandlerType handlerType) {
        return Map.copyOf(registry.getOrDefault(handlerType, Map.of()));
    }

    @NonNull
    public List<DomainEventHandler<E>> getHandlers(@NonNull HandlerType handlerType,
                                                   @Nullable String aggregateType,
                                                   @NonNull Class<?> domainEventClass) {
        return registry.getOrDefault(handlerType, Map.of())
            .getOrDefault(aggregateType, emptyList())
            .stream()
            .filter(handler -> handler.getEventClass().isAssignableFrom(domainEventClass))
            .collect(toList());
    }
}
