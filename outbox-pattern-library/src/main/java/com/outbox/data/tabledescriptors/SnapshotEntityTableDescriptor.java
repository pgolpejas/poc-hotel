package com.outbox.data.tabledescriptors;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SnapshotEntityTableDescriptor extends BaseTableDescriptor {

    public SnapshotEntityTableDescriptor() {
        super();
    }

    @Override
    public String getTableName() {
        return "snapshot";
    }

}
