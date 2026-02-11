package org.bit.bitgram.global.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // 이 어노테이션이 붙은 클래스는 테이블로 생성 x
@EntityListeners(value = AuditingEntityListener.class) // BaseEntity의 변화(insert, update) 감지
public abstract class CreatedTimeEntity {
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
