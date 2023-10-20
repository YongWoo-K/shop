package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderItemRepository;
import com.shop.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class OrderTest {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em;

    public Item createItem(){
        Item item = new Item();
        item.setItemName("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }

    @Test
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest(){
        Order order = new Order();

        for(int i = 0; i < 3; i++){
            Item item = this.createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);   //orderItem 엔티티를 order 엔티티에 담아준다
        }

        orderRepository.saveAndFlush(order);    // order 엔티티를 영속성 컨텍스트에 저장 + 데이터베이스에 반영
        em.clear(); // 영속성 컨텍스트 초기화

        Order savedOrder = orderRepository.findById(order.getId())
                .orElseThrow(EntityNotFoundException::new); // order 엔티티 조회

        assertEquals(3, savedOrder.getOrderItems().size()); 
        //  -> 조회한 order 엔티티의 갯수가 3개인지 검사(createItem()으로 만든 상품이 3개이므로)
    }

    @Autowired
    MemberRepository memberRepository;

    public Order createOrder(){
        Order order = new Order();

        for(int i = 0; i < 3; i++){
            Item item = createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }

        Member member = new Member();
        memberRepository.save(member);

        order.setMember(member);
        orderRepository.save(order);
        return order;
    }

    @Test
    @DisplayName("고아객체 제거 테스트")
    public void orphanRemovalTest(){
        Order order = this.createOrder();
        order.getOrderItems().remove(0); //order 엔티티 속 orderItem 엔티티의 0번째 요소 제거
        em.flush();
        //-> flush() 호출 시 orderItem 엔티티 삭제 쿼리문 실행
        //-> order 엔티티 속 orderItem 엔티티의 0번째 요소가 제거되어 order 엔티티와의 연관관계가 끊어졌기 때문
    }

    @Autowired
    OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLoadingTest(){
        Order order = this.createOrder();
        Long orderItemId = order.getOrderItems().get(0).getId(); //order 엔티티 속 orderItem 엔티티 중 0번째 요소의 id
        em.flush();
        em.clear();

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(EntityNotFoundException::new);
        //-> order 엔티티 속 orderItem_id를 이용하여 데이터베이스에서 orderItem을 조회

        System.out.println("Order class : " + orderItem.getOrder().getClass());
        //-> 조회한 orderItem 엔티티에 있는 order 객체의 클래스를 출력
        System.out.println("==================================");
        orderItem.getOrder().getOrderDate();
        System.out.println("==================================");
    }

}