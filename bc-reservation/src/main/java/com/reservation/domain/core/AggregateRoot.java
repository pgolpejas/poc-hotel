package com.reservation.domain.core;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class AggregateRoot {

  @EqualsAndHashCode.Exclude
  @ToString.Exclude
  private List<DomainEvent> domainEvents;

  protected void registerEvent(final DomainEvent event) {

    if (this.domainEvents == null) {
      this.domainEvents = new ArrayList<>(1);
    }
    this.domainEvents.add(event);
  }

  public List<DomainEvent> domainEvents() {

    if (this.domainEvents == null) {
      return List.of();
    }
    return Collections.unmodifiableList(this.domainEvents);
  }

  public abstract String getAggregateId();
}
