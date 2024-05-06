package com.outbox.aop;

import com.outbox.annotation.Outbox;
import com.outbox.data.OutboxContext;
import com.outbox.data.OutboxRuntimeException;
import com.outbox.stream.preprocessors.AggregateIdentifierNotFound;
import com.outbox.stream.preprocessors.OutboxEntityPreProcessor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Value
@Slf4j
public class OutboxAspect {

    @SuppressWarnings("rawtypes")
    private Set<OutboxEntityPreProcessor> preProcessors;

    private ConcurrentHashMap<String, Method> methodExtractorCache = new ConcurrentHashMap<>();

    @Around("@annotation(outboxAnnotation)")
    public Object transactionalOutbox(final ProceedingJoinPoint joinPoint, final Outbox outboxAnnotation)
            throws Throwable {

        log.debug("Target: {} Method: {} PullEventsFrom: {}", joinPoint.getTarget().getClass(),
                joinPoint.getSignature(), outboxAnnotation.pullEventsFrom());
        if (this.hasMoreThanOneParameter(joinPoint)) {
            throw new IllegalArgumentException(" method: " + joinPoint.getSignature() + " has more than one parameter");
        }

        final Object aggregateRoot = joinPoint.getArgs()[0];

        Object proceed;
        try {
            proceed = joinPoint.proceed();
        } catch (OutboxRuntimeException ore) {
            log.error("There was an OutboxRuntimeException", ore);
            throw ore;
        } catch (Throwable me) {
            log.error("There was a business exception", me);
            throw me;
        }

        String aggregateId = getAggregateId(aggregateRoot, outboxAnnotation.getAggregatedIdFrom());

        OutboxContext context = new OutboxContext(outboxAnnotation.pullEventsFrom(), aggregateId);
        try {
            preProcessors.forEach(preProcessor -> preProcessor.preProcess(aggregateRoot, context));
        } catch (Exception e) {
            log.error("There was an error preprocessing outbox entities", e);
            throw e;
        }
        return proceed;
    }

    private boolean hasMoreThanOneParameter(final ProceedingJoinPoint joinPoint) {
        return joinPoint.getArgs().length > 1;
    }

    private String getAggregateId(Object aggregateObject, String getIdFromMethod) throws AggregateIdentifierNotFound {
        String result;
        try {
            Method methodToExtractId = searchInvokeMethodToGetId(aggregateObject, getIdFromMethod);
            if (methodToExtractId == null) {
                throw new AggregateIdentifierNotFound(
                        "There was and error trying to recover aggregate identifier from the object object using method:"
                                + getIdFromMethod + ", method not exist");

            }
            result = methodToExtractId.invoke(aggregateObject).toString();

        } catch (Exception me) {
            throw new AggregateIdentifierNotFound(
                    "There was and error trying to recover aggregate identifier from the object object using method:{}"
                            + getIdFromMethod,
                    me);
        }
        if (result == null) {
            throw new AggregateIdentifierNotFound(
                    "It was impossible to recover aggregate identifier from the aggregate object event using method:"
                            + getIdFromMethod);
        }
        return result;
    }

    private Method searchInvokeMethodToGetId(Object aggregateObject, String getIdFromMethod) {
        String searchKey = aggregateObject.getClass().getName() + getIdFromMethod;

        Method methodToExtractId = methodExtractorCache.get(searchKey);
        if (methodToExtractId == null) {
            Method[] methods = aggregateObject.getClass().getMethods();
            for (Method method : methods) {
                if (method.getName().equals(getIdFromMethod)) {
                    methodToExtractId = method;
                    methodExtractorCache.put(searchKey, methodToExtractId);
                    break;
                }
            }
        }
        return methodToExtractId;
    }

}
