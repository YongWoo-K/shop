package com.shop.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass   //공통 매핑 정보가 필요할때 사용하는 어노테이션(부모클래스를 상속받는 자식클래스의 매핑정보만 제공)
@EntityListeners(value = {AuditingEntityListener.class})    //Auditing 기능을 사용하기위해 사용
@Getter @Setter
public abstract class BaseTimeEntity {

    @CreatedDate //해당 엔티티가 생성되어 저장될 때 시간을 자동으로 저장하는 어노테이션
    @Column(updatable = false) //해당 엔티티의 수정을 불가하도록 설정
    private LocalDateTime regTime;  //등록일

    @LastModifiedDate //해당 엔티티의 값을 변경할 때 시간을 자동으로 저장하는 어노테이션
    private LocalDateTime updateTime;   //수정일
}
