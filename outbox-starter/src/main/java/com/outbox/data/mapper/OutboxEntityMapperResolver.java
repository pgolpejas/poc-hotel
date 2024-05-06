package com.outbox.data.mapper;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class OutboxEntityMapperResolver {

    @SuppressWarnings("rawtypes")
    private final Map<Class<?>, List<OutboxEntityMapper>> outboxEntityMappersMap = new HashMap<>();

    @SuppressWarnings("rawtypes")
    public OutboxEntityMapperResolver(List<OutboxEntityMapper<?>> outboxEntityMappers) {

        outboxEntityMappers.forEach(oem -> {
            Class<?> oemClass = extracted(oem);
            @SuppressWarnings("rawtypes")
            List<OutboxEntityMapper> mappersForThisClass = outboxEntityMappersMap.get(oemClass);
            if (mappersForThisClass == null) {

                mappersForThisClass = new ArrayList<>();
            }
            mappersForThisClass.add(oem);
            outboxEntityMappersMap.put(oemClass, mappersForThisClass);
        });

    }

    private Class<?> extracted(OutboxEntityMapper<?> outboxEntityMapper) {
        return (Class<?>) ((ParameterizedType) outboxEntityMapper.getClass().getGenericInterfaces()[0])
            .getActualTypeArguments()[0];
    }

    @SuppressWarnings("rawtypes")
    public List<OutboxEntityMapper> resolve(Class<?> clazz) {
        return outboxEntityMappersMap.get(clazz);
    }

}
