package com.shop.service;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.dto.CartOrderDto;
import com.shop.dto.OrderDto;
import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.repository.CartItemRepository;
import com.shop.repository.CartRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {
    
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;
    
    //장바구니에 상품을 담는 로직
    public Long addCart(CartItemDto cartItemDto, String email){
        //장바구니에 담을 상품 엔티티 조회
        Item item = itemRepository.findById(cartItemDto.getItemId()).orElseThrow(EntityNotFoundException::new);
        
        //현재 로그인한 회원 엔티티 조회
        Member member = memberRepository.findByEmail(email);
        
        //현재 로그인한 회원의 장바구니 엔티티 조회
        //  -> 회원의 장바구니가 비어있을 경우 새로운 장바구니 엔티티 생성
        Cart cart = cartRepository.findByMemberId(member.getId());
        if(cart == null){
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }
        
        //장바구니에 상품이 들어있는지 조회
        //  -> 장바구니에 이미 상품이 들어있는 경우 기존 수량에 새로 추가할 수량만큼 더해줌
        //  -> 장바구니가 비어있을 경우 장바구니 엔티티, 상품 엔티티, 담을 수량을 이용하여 새로 CartItem 엔티티를 생성 후 저장
        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());
        if(savedCartItem != null){
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();
        } else{
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }

    //장바구니에 들어있는 상품을 조회하는 로직
    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email){

        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);
        
        //현재 로그인한 회원의 장바구니 엔티티 조회
        Cart cart = cartRepository.findByMemberId(member.getId());
        
        //장바구니에 상품을 안담았을 경우 장바구니 엔티티가 없으므로 빈 리스트를 리턴
        if(cart == null){
            return cartDetailDtoList;
        }
        
        //장바구니에 담겨있는 상품 정보들을 조회
        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
        
        //조회한 장바구니 상품 정보를 리턴
        return cartDetailDtoList;
    }
    
    //현재 로그인한 회원과 해당 장바구니 상품을 저장한 회원이 같은지 검사하는 로직
    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email){
        //현재 로그인한 회원 조회
        Member curMember = memberRepository.findByEmail(email);
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);
        
        //장바구니 상품을 저장한 회원 조회
        Member savedMember = cartItem.getCart().getMember();
        
        //현재 로그인한 회원과 장바구니 상품을 저장한 회원이 다를 경우 false를, 같을 경우 true를 리턴
        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }
        
        return true;
    }
    
    //장바구니 상품 수량 업데이트 로직
    public void updateCartItemCount(Long cartItemId, int count){
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);

        cartItem.updateCount(count);
    }

    //장바구니에서 상품 삭제 로직
    public void deleteCartItem(Long cartItemId){
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }
    
    //주문 로직으로 전달할 orderDto 리스트 생성 및 주문 로직 호출, 주문한 상품은 장바구니에서 제거하는 로직
    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email){
        List<OrderDto> orderDtoList = new ArrayList<>();
        
        //장바구니 페이지에서 전달받은 주문 상품 번호를 이용, 주문 로직으로 전달할 orderDto 객체를 생성
        for(CartOrderDto cartOrderDto : cartOrderDtoList){
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId()).orElseThrow(EntityNotFoundException::new);
            
            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }
        
        //장바구니에 담은 상품을 주문하도록 주문 로직 호출
        Long orderId = orderService.orders(orderDtoList, email);
        
        //주문한 상품들을 장바구니에서 제거
        for(CartOrderDto cartOrderDto : cartOrderDtoList){
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId()).orElseThrow(EntityNotFoundException::new);
            cartItemRepository.delete(cartItem);
        }
        
        //주문 번호 리턴
        return orderId;
    } 
}
