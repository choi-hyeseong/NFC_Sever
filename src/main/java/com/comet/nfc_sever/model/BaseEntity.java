package com.comet.nfc_sever.model;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
@Getter
public class BaseEntity {

    @CreatedDate
    private LocalDateTime createdTime; //create, update 는 예약어라 충돌생길 수도 있음.

    @LastModifiedDate
    private LocalDateTime updateTime;

}
