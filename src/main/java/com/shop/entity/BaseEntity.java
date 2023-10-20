package com.shop.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

@EntityListeners(value = {AuditingEntityListener.class})
@MappedSuperclass
@Getter
public abstract class BaseEntity extends BaseTimeEntity{

    @CreatedBy  //해당 엔티티가 생성되어 저장될 때 사용자를 자동으로 저장하는 어노테이션
    @Column(updatable = false)
    private String createdBy;    //등록자

    @LastModifiedBy //해당 엔티티의 값이 수정될 때 수정자를 자동으로 저장하는 어노테이션
    private String modifiedBy;  //수정자

    //-> BaseEntity는 BaseTimeEntity를 상속받고 있다.
    //-> 따라서 등록일, 수정일, 등록자, 수정자를 모두 갖는 엔티티는 BaseEntity를 상속받으면 된다.
    //-> 등록일, 수정일만 갖는 엔티티는 BaseTimeEntity를 상속받으면 된다.
}
