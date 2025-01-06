package com.hotel.core.infrastructure.database.audit;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditJpa {

  @Column(name = "created_at", nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  private LocalDateTime createdAt;

  @Column(name = "modified_at", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  private LocalDateTime modifiedAt;

  @LastModifiedBy
  @Column(name = "modified_by")
  private String modifiedBy;

}
