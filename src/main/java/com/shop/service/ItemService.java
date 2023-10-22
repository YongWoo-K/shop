package com.shop.service;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemImgDto;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;

    //상품 등록
    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{

        //상품 등록 폼으로부터 입력받은 데이터를 이용하여 item 객체를 생성 후 저장
        Item item = itemFormDto.createItem();
        itemRepository.save(item);

        //이미지 등록
        for(int i = 0; i < itemImgFileList.size(); i++){
            //itemImg 객체를 생성하여 입력받은 데이터를 저장
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            
            //첫번째 이미지일 경우 대표 상품 이미지 여부 값을 Y로, 나머지 상품 이미지는 N으로 세팅
            if(i == 0){
                itemImg.setRepImgYn("Y");
            } else{
                itemImg.setRepImgYn("N");
            }
            
            //상품 이미지 정보 저장
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }

        return item.getId();
    }

    //상품 조회
    @Transactional(readOnly = true) //-> 상품 데이터를 읽어오는 트랜잭션을 읽기전용으로 설정(데이터의 수정이 일어나지 않기 때문)
    public ItemFormDto getItemDtl(Long itemId){
        
        //상품 아이디를 통해 해당 상품의 이미지를 조회
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        
        //조회한 ItemImg 엔티티를 ItemImgDto 객체로 만들어서 리스트에 추가
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for(ItemImg itemImg : itemImgList){
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }
        
        //상품의 아이디를 통해 상품 엔티티를 조회
        //  -> 존재하지 않을 경우 EntityNotFoundException을 발생
        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
        
        //조회한 상품 엔티티와 상품 이미지 엔티티를 ItemFotmDto 객체에 저장 후 리턴
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;
    }

    //상품 수정
    public Long updateitem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception{
        //상품 등록 화면으로부터 전달받은 상품 아이디를 이용하여 상품 엔티티를 조회
        Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(EntityNotFoundException::new);

        //상품 등록 화면으로부터 전달받은 ItemFormDto를 통해 상품 엔티티를 업데이트
        item.updateItem(itemFormDto);

        //해당 상품 이미지들의 아이디들을 조회
        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        //상품 이미지를 업데이트 하기 위해 updateItemImg() 메소드에 상품 이미지 아이디와 상품 이미지 파일 정보를 파라미터로 전달한다.
        for(int i = 0; i < itemImgFileList.size(); i++){
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }
        
        //수정이 완료된 상품의 아이디를 리턴
        return item.getId();
    }

    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }
}
