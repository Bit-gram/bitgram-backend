package org.bit.bitgram.global.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(value = AuditingEntityListener.class)
public abstract class ModifiedTimeEntity extends CreatedTimeEntity {
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
