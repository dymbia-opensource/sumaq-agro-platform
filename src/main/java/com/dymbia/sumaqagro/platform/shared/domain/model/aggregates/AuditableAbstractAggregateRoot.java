package com.dymbia.sumaqagro.platform.shared.domain.model.aggregates;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableAbstractAggregateRoot<T extends AuditableAbstractAggregateRoot<T>> extends AbstractAggregateRoot<T>{

    @CreatedDate
    @Column(nullable=false,updatable=false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable=false)
    private Instant updatedAt;
}
