package com.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "order_item")
@Getter @Setter
public class OrderItem extends BaseEntity{
    
    @Id
    @Column(name = "order_item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //즉시로딩으로 설정시 매핑된 모든 엔티티를 함께 가지고 오기 때문에 실행 결과를 예측하기 어렵고 성능도 떨어진다
    //  -> 따라서 즉시로딩을 사용하는 대신 지연로딩을 사용한다.
    //  -> fetch 속성을 FetchType.LAZY로 설정하여 사용한다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    
   private int orderPrice;  //주문 가격
   private int count;       //수량
}
