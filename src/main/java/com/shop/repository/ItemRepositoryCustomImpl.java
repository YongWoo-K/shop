package com.shop.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{
//-> ItemRepositoryCustom를 상속받음

    private JPAQueryFactory queryFactory;
    //-> 동적으로 쿼리를 생성하기위해 JPAQueryFactory 클래스 사용

    //JPAQueryFactory의 생성자로 EntityManager 객체를 넣어줌
    public ItemRepositoryCustomImpl(EntityManager em){
        this.queryFactory = new JPAQueryFactory(em);
    }

    //상품 판매 조건이 전체(null)일 경우 null을 리턴
    //  -> 상품 판매 조건이 null이라면 where절에서 해당 조건은 무시
    //  -> 상품 판매 조건이 null이 아니라 판매중 or 품절 상태라면 해당 조건의 상품만 조회
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    //searchDateType의 값에 따라 dateTime의 값을 이전 시간의 값으로 세팅 후 해당 시간 이후로 등록된 상품만 조회한다.
    //-> ex) searchDateType 값이 "1m"인 경우 dateTime의 시간을 한 달 전으로 세팅 후 최근 한 달 동안 등록된 상품을 조회하도록 조건값을 리턴한다.
    private BooleanExpression regDtsAfter(String searchDateType){
        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all", searchDateType) || searchDateType == null){
            return null;
        } else if(StringUtils.equals("1d", searchDateType)){
            dateTime = dateTime.minusDays(1);
        } else if(StringUtils.equals("1w", searchDateType)){
            dateTime = dateTime.minusWeeks(1);
        } else if(StringUtils.equals("1m", searchDateType)){
            dateTime = dateTime.minusMonths(1);
        } else if(StringUtils.equals("6m", searchDateType)){
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime);
    }
    
    //searchBy의 값에 따라 상품명에 검색어를 포함하고 있는 상품 또는 상품 생성자의 아이디에 검색어를 포함하고 있는 상품을 조회하도록 조건값을 리턴
    private BooleanExpression searchByLike(String searchBy, String searchQuery){
        if(StringUtils.equals("itemName", searchBy)){
            return QItem.item.itemName.like("%" + searchQuery + "%");
        } else if(StringUtils.equals("createdBy", searchBy)){
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }

        return null;
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        //queryFactory를 이용해서 쿼리를 생성
        //  - selectFrom(QItem.item) : 상품 데이터 조회를 위해 QItem의 item을 지정
        //  - where 조건절 : BooleanExpresstion 반환하는 조건문들을 넣어준다. ',' 단위로 넣어줄 경우 and 조건으로 인식
        //  - offset : 데이터를 가지고 올 시작 인덱스를 지정
        //  - limit : 한 번에 가지고올 최대 개수를 지정
        //  - fetchResult() : 조회한 리스트 및 전체 개수를 포함하는 QueryResults를 리턴
        //      -> 조회 대상 리스트 조회 및 상품 데이터 전체 개수를 조회하는 2번의 쿼리문 실행
        QueryResults<Item> results = queryFactory
                .selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Item> content = results.getResults();
        Long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
        //-> 조회한 데이터를 Page 클래스의 구현체인 PageImpl 객체로 리턴
    }
}
