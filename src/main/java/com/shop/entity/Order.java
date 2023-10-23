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
    
    //상품 주문 정보를 담음
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
        //-> Order 엔티티와 OrderItem 엔티티가 양방향 참조 관계이므로 orderItem 객체에도 order 객체를 세팅
    }
    
    //주문
    public static Order createOrder(Member member, List<OrderItem> orderItemList){
        Order order = new Order();
        order.setMember(member);    //상품을 주문한 회원의 정보
        for(OrderItem orderItem : orderItemList){
        //-> 장바구니에서는 여러 개의 상품을 주문할 수 있으므로 리스트를 파라미터로 받음
            order.addOrderItem(orderItem);
        }
        order.setOrderStatus(OrderStatus.ORDER);    //주문 상태
        order.setOrderDate(LocalDateTime.now());    //현재 시간을 주문 시간으로 세팅
        return order;
    }

    //총 주문 금액
    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
    
    //주문 취소시 상품 재고 원상복구 + 주문 상태를 취소로 변경
    public void cancelOrder(){
        this.orderStatus = OrderStatus.CANCLE;
        
        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }
}
