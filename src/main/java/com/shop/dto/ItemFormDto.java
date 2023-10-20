package com.shop.dto;

import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ItemFormDto {

    private Long id;

    @NotBlank(message = "상품명은 필수 입력 사항입니다.")
    private String itemName;

    @NotNull(message = "가격은 필수 입력 사항입니다.")
    private Integer price;

    @NotBlank(message = "상세내용은 필수 입력 사항입니다.")
    private String itemDetail;

    @NotNull(message = "재고는 필수 입력 사항입니다.")
    private Integer stockNumber;

    private ItemSellStatus itemSellStatus;

    private List<ItemImgDto> itemImgDtoList = new ArrayList<>();    //상품 이미지 정보 리스트

    private List<Long> itemImgIds = new ArrayList<>();              //상품 이미지 아이디 리스트

    private static ModelMapper modelMapper = new ModelMapper();

    public Item createItem(){
        return modelMapper.map(this, Item.class);
        //-> modelMapper를 이용하여 엔티티객체와 DTO객체 간의 데이터를 복사 후 복사한 객체를 반환해주는 메소드
    }

    public static ItemFormDto of(Item item){
        return modelMapper.map(item, ItemFormDto.class);
        //-> modelMapper를 이용하여 엔티티객체와 DTO객체 간의 데이터를 복사 후 복사한 객체를 반환해주는 메소드
        //-> Item 엔티티 객체를 파라미터로 받아서 Item 객체의 자료형과 멤버변수의 이름이 같을 때 ItemFormDto로 값을 복사한 후 복사한 객체를 리턴
        //-> static으로 선언한 메소드이므로 내부에 ItemFormDto 객체를 생성하지 않아도 사용이 가능하다.
    }
}
