package com.outbox.data;

import org.slf4j.Logger;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public interface OutboxRepository<E extends BaseEventEntity> {

    void save(E outboxEntity);

    List<E> findNotPublished();

    List<E> findFirstNotPublishedSince(Long maxWait);

    int removeOlderThan(Integer maxWait);

    List<E> findByAggregateIdAndAggregateType(String id, String type);

    List<AggregateResume> findDistinctAggregates();

    List<E> findPendingByAggregateTypeOlderThan(String aggregateId, List<String> eventId, String type, Instant instant);

    Stream<E> findByAggregateTypeBetweenDates(Instant from, Instant to, String parent);

    List<E> findByAggregateId(String aggregateId);

    E findLatestByAggregateRoot(String aggregateRoot);

    abstract E instantiateNew();

    default void invokeSetter(Object obj, String propertyName, Object variableValue, Logger log) {
        PropertyDescriptor pd;
        try {
            pd = new PropertyDescriptor(propertyName, obj.getClass());
            Method setter = pd.getWriteMethod();
            boolean converted = false;
            if (variableValue != null) {

                if (List.of(setter.getParameterTypes()).contains(Instant.class)
                        && variableValue.getClass().equals(Timestamp.class)) {
                    // Try to convert
                    Timestamp variableValueT = ((Timestamp) variableValue);
                    setter.invoke(obj, variableValueT.toInstant());
                    converted = true;
                } else if (List.of(setter.getParameterTypes()).contains(Instant.class)
                        && variableValue.getClass().equals(Date.class)) {
                    // Try to convert
                    Date variableValueT = ((Date) variableValue);
                    setter.invoke(obj, variableValueT.toInstant());
                    converted = true;
                } else if (List.of(setter.getParameterTypes()).contains(UUID.class)
                        && variableValue.getClass().equals(String.class)) {
                    // Try to convert
                    String variableValueS = variableValue.toString();
                    setter.invoke(obj, UUID.fromString(variableValueS));
                    converted = true;
                }
            }
            if (!converted) {
                setter.invoke(obj, variableValue);
            }
        } catch (Exception e) {
            log.error("Error {} accesing field:{}", e.getMessage(), propertyName);
        }

    }

    @SuppressWarnings("rawtypes")
    default Object invokeGetter(Object obj, String variableName, Class variableDestinationType, Logger log) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(variableName, obj.getClass());
            Method getter = pd.getReadMethod();
            if (getter.getReturnType().equals(Instant.class)
                    && variableDestinationType.equals(Timestamp.class)) {
                // Try to convert
                Instant e = (Instant) getter.invoke(obj);
                if (e != null) {
                    return Timestamp.from(e);
                } else {
                    return null;
                }
            } else if (getter.getReturnType().equals(UUID.class)
                    && variableDestinationType.getClass().equals(String.class)) {
                // Try to convert
                UUID u = (UUID) getter.invoke(obj);
                if (u != null) {
                    return u.toString();
                } else {
                    return null;
                }
            }
            return getter.invoke(obj);
        } catch (Exception e) {
            log.error("Error {} accesing field:{}", e.getMessage(), variableName);
        }
        return null;
    }

    void removeAll();

    boolean canStore(Class<?> eventClass);

    void remove(E entity);

    Long countAll();

    List<E> findById(String id);

    boolean exists(String id);

    Stream<E> streamOlderThan(Integer maxWaitr, Integer limit);

    Long countOlderThan(Integer maxWaitr);

    Stream<E> streamAll();

    Stream<E> findByAggregateRoot(String aggregateRoot);

    List<E> findOlderThan(Integer maxWait, Integer limit);

    List<E> pageAll(Integer offset, Integer limit);

}
