package com.shop.entity;

import com.shop.dto.MemberFormDto;
import com.shop.repository.CartRepository;
import com.shop.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class CartTest {
    @Autowired
    CartRepository cartRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    //@PersistenceContext : 영속성 컨텍스트를 주입하는 표준 어노테이션
    //  -> JPA의 @Autowired 라고 보면 된다.
    //  -> 여기서는 영속성 컨텍스트를 사용하기 위해 해당 어노테이션을 사용하여 EntityManager를 주입했다.
    @PersistenceContext
    EntityManager em;

    public Member createMember(){
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test@email.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 마포구 합정동");
        memberFormDto.setPassword("1234");
        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("장바구니 회원 엔티티 매핑 조회 테스트")
    public void findCartAndMemberTest(){
        Member member = createMember();
        memberRepository.save(member); //영속성 컨텍스트에 멤버엔티티 저장

        Cart cart = new Cart();
        cart.setMember(member);
        cartRepository.save(cart); //영속성 컨텍스트에 장바구니엔티티 저장

        em.flush(); //영속성 컨텍스트에 저장된 엔티티를 데이터베이스에 반영
        em.clear(); //영속성 컨텍스트 초기화
        
        //저장된 장바구니 엔티티 조회
        //  -> 먼저 영속성 컨텍스트로부터 엔티티를 조회하고, 없으면 데이터베이스를 조회
        Cart savedCart = cartRepository.findById(cart.getId()).orElseThrow(EntityNotFoundException::new);
        
        //저장한 엔티티와 매핑한 엔티티의 id가 같은지 비교
        assertEquals(savedCart.getMember().getId(), member.getId());
    }
}