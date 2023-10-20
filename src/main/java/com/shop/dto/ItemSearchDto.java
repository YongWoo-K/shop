package com.shop.dto;

import com.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemSearchDto {
    
    private String searchDateType;  //현재시간과 상품등록일을 비교하여 상품 조회
    private ItemSellStatus searchSellStatus;    // 상품의 판매상태를 기준으로 상품 조회
    private String searchBy;    //상품 조회시 어떤 유형(상품명 or 상품등록자)으로 조회할지 선택
    private String searchQuery = "";    //조회할 검색어를 저장할 변수

}
