package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import com.shop.exception.OutOfStockException;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "item")
@Getter @Setter
@ToString
public class Item extends BaseEntity{
    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;                        //상품코드
    //-> id는 primary key 이므로 @id 어노테이션을 붙여준다.
    //-> 이후 @GeneratedValue 어노테이션을 통해 primary key 생성전략을 AUTO로 설정한다.

    //상품 관련 설명들은 반드시 필요한 내용이므로 nullable을 false로 설정(= not null)하여 반드시 입력하도록 설정한다.
    @Column(nullable = false, length = 50)
    private String itemName;                //상품명

    @Column(name = "price", nullable = false)
    private int price;                      //상품가격

    @Column(nullable = false)
    private int stockNumber;                //상품재고수량

    @Lob
    @Column(nullable = false)
    private String itemDetail;              //상품상세설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;  //상품판매상태

    public void updateItem(ItemFormDto itemFormDto){
        this.itemName = itemFormDto.getItemName();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }
    
    //주문 후 재고 수량(재고가 주문보다 작을 경우 예외 발생)
    public void removeStock(int stockNumber){
        int restStock = this.stockNumber - stockNumber;
        if(restStock < 0){
            throw new OutOfStockException("상품의 재고가 부족합니다.(현재 재고 수량 : " + this.stockNumber + ")");
        }
        this.stockNumber = restStock;
    }
    
    //주문 취소시 상품 재고 원상 복구
    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
    }
}
