package com.shop.repository;

import com.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item>, ItemRepositoryCustom {
    //상품명으로 조회
    List<Item> findByItemName(String itemName);

    //상품명 + 상품상세설명으로 조회
    List<Item> findByItemNameOrItemDetail(String itemName, String itemDetail);

    //특정가격이하의 상품 조회
    List<Item> findByPriceLessThan(Integer price);

    //특정가격이하의 상품을 OrderBy로 정렬해서 조회
    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);
    
    //@Query 어노테이션을 이용한 검색
    //-> @Query 어노테이션에 JPQL로 작성한 쿼리문을 넣어준다.
    //-> 엔티티클래스로 작성한 Item으로부터 데이터를 select 하겠다는 의미
    //-> 파라미터에 @Param 어노테이션을 사용해서 파라미터로 넘어온 값을 JPQL에 들어갈 변수로 지정했다.
    //-> 여기에서는 %%사이에 ":itemDetail"로 값이 들어가도록 설정
    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);

    //@Query의 nativeQuery 속성을 사용한 검색
    //-> 해당 속성 사용시 기존 쿼리를 그대로 활용할 수 있다.
    //-> 이 경우 해당 데이터베이스에 종속되게 되므로 복잡한 쿼리를 그대로 사용해야하는 특수한 경우에만 사용한다.
    //-> 여기서는 해당 데이터베이스의 item 테이블의 모든 데이터를 select 했다.
    @Query(value = "select * from item i where i.item_detail like %:itemDetail% order by i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);

}
