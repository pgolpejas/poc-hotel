package com.outbox.annotation;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Transactional(propagation = Propagation.REQUIRED)
public @interface Outbox {

    String pullEventsFrom() default "domainEvents";

    String getAggregatedIdFrom() default "getAggregateId";

}
