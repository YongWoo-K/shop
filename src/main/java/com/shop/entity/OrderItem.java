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
    
    //상품 주문
    public static OrderItem createOrderItem(Item item, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);    //주문할 상품 세팅
        orderItem.setCount(count);  //주문 수량 세팅
        orderItem.setOrderPrice(item.getPrice()); //현재 시간을 기준으로 상품가격을 주문가격으로 세팅

        item.removeStock(count);    //주문 수량만큼 상품 재고 감소
        return orderItem;
    }
    
    //해당 상품 주문 시 총 가격
    public int getTotalPrice(){
        return orderPrice * count;
    }
    
    //주문 취소시 상품 재고 원상 복구
    public void cancel(){
        this.getItem().addStock(count);
    }
}
