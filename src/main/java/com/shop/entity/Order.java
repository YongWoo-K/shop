package com.shop.entity;

import com.shop.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order extends BaseEntity{

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate;    //주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;    //주문 상태

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();
    //  -> 외래키(order_id)가 order_item 테이블에 있으므로 연관관계의 주인은 OrderItem 엔티티이다.
    //  -> Order 엔티티는 주인이 아니므로 mappedBy 속성으로 연관관계의 주인을 설정한다.
    //  -> 이때 속성값으로 order를 사용한 이유는 OrderItem에 있는 order에 의해 관리되기 때문이다.
    //  -> 연관 관계 주인의 필드인 order를 mappedBy의 속성값으로 세팅한 것
    //  -> CascadeType.ALL : 부모 엔티티의 영속성 상태 변화를 자식 엔티티에게 모두 전이하는 옵션
    //  -> orphanRemoval = true : 고아 객체 제거 옵션
    //      -> 고아객체 : 부모 엔티티와 연관 관계가 끊어진 자식 엔티티
    //  -> 하나의 주문이 여러개의 주문 상품을 갖으므로 List 자료형을 사용하여 매핑

}
