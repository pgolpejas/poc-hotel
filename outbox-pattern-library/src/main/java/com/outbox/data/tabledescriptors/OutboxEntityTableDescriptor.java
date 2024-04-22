package com.outbox.data.tabledescriptors;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class OutboxEntityTableDescriptor extends BaseTableDescriptor {


    public OutboxEntityTableDescriptor() {
        super();
    }

    @Override
    public String getTableName() {
        return "outbox";
    }

}
