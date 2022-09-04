package net.vvsh.domainevent.core.entity.converter;

import static org.springframework.util.StringUtils.hasText;

import net.vvsh.domainevent.core.domain.DomainEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.springframework.util.ClassUtils;

@Converter
public class DomainEventConverter implements AttributeConverter<DomainEvent, String> {

    private static final String TYPE_COLUMN = "_type";
    private static final TypeReference<Map<String, Object>> OBJECT_MAP = new TypeReference<>() {
    };

    private final ConcurrentMap<String, Class<?>> idClassMappings = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public DomainEventConverter() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    @Override
    public String convertToDatabaseColumn(DomainEvent domainEvent) {
        if (domainEvent == null) {
            return null;
        }

        try {
            Map<String, Object> objectMap = objectMapper.convertValue(domainEvent, OBJECT_MAP);
            objectMap.put(TYPE_COLUMN, domainEvent.getClass().getName());

            return objectMapper.writeValueAsString(objectMap);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to serialize domain event into json payload", e);
        }
    }

    @Override
    public DomainEvent convertToEntityAttribute(String dbValue) {
        if (!hasText(dbValue)) {
            return null;
        }

        try {
            Map<String, Object> objectMap = objectMapper.readValue(dbValue, OBJECT_MAP);
            JavaType javaType = getJavaTypeForValue(objectMap);

            return objectMapper.readValue(dbValue, javaType);
        } catch (Exception e) {
            throw new RuntimeException("Unable to deserialize payload into domain event", e);
        }
    }

    protected JavaType getJavaTypeForValue(Map<String, Object> objectMap) {
        String typeId = (String) objectMap.get(TYPE_COLUMN);
        if (typeId == null) {
            throw new IllegalArgumentException("Unable to get `_type` property from the object");
        }

        Class<?> mappedClass = idClassMappings.computeIfAbsent(typeId, id -> {
            try {
                return ClassUtils.forName(id, Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Failed to resolve type id [%s]".formatted(id), e);
            }
        });

        return objectMapper.constructType(mappedClass);
    }
}
