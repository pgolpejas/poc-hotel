package com.hotel.core.domain.ddd;

import java.io.Serializable;

public interface DomainEvent extends Serializable {

  String getAggregateId();

  String getActionType();

}
