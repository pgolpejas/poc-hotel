package com.outbox.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AggregateResume {

    private Integer count = 0;

    private String aggregate;

}
