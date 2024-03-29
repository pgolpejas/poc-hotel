package com.reservation.domain.core;

import java.io.Serializable;

public interface DomainEvent extends Serializable {

  String getAggregateId();

  String getActionType();

}
