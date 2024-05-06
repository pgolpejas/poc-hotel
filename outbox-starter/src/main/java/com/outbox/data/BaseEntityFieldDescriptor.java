package com.outbox.data;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class BaseEntityFieldDescriptor {


    private String mongoColumnName;

    private String jdbcColumnName;

    private String fieldName;

    @SuppressWarnings("rawtypes")
    private Class type;

    private boolean updatable;

    private boolean primary;

}

