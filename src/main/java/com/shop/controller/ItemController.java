package com.shop.controller;

import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import com.shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model){
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "/item/itemForm";
    }

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model,
                          @RequestParam("itemImgFile")List<MultipartFile> itemImgFileList){
        
        //상품 등록 시 필수 입력 사항이 없다면 다시 상품 등록 페이지로 전환 
        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }
        
        //상품 등록 시 첫번째 이미지가 없다면 에러메세지와 함께 다시 상품 등록 페이지로 전환
        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 사항 입니다.");
            return "item/itemForm";
        }
        
        //상품 저장 로직 호출
        //  -> 매개변수로 상품 정보(itemFormDto)와 상품 이미지 정보(itemImgFileList)를 넘겨준다
        try{
            itemService.saveItem(itemFormDto, itemImgFileList);
        } catch(Exception e){
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생했습니다.");
            return "item/itemForm";
        }
        
        //정상적으로 상품 등록 시 메인 페이지로 이동
        return "redirect:/";
    }
    
    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model){
        //조회한 상품 데이터를 모델에 담아서 전달
        try{
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        } catch(EntityNotFoundException e){
            //상품엔티티가 존재하지 않을 경우 에러메세지를 담아서 상품등록페이지로 이동
            model.addAttribute("errorMessage", "존재하지 않는 상품입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "item/itemForm";
        }
        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/{itemId}")
    public String updateItem(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model){
        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }
        
        //첫번째 상품 이미지 미입력시 에러 발생
        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 사항입니다.");
            return "item/itemForm";
        }

        //상품 수정 로직을 호출
        try{
            itemService.updateitem(itemFormDto, itemImgFileList);
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생했습니다.");
            return "item/itemForm";
        }

        return "item/itemForm";
    }
    
    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    //-> value에 상품 관리 화면 진입 시 URL에 페이지 번호가 없는 경우와 있는 경우 2가지를 매핑한다.
    public String itemManage(ItemSearchDto itemSearchDto, 
                             @PathVariable("page")Optional<Integer> page, Model model){
        //페이징을 위해 PageRequest.of() 메소드를 통해 Pageable 객체를 생성한다.
        //  -> 첫번째 파라미터로 조회할 페이지 번호, 두번째 파라미터로 한 번에 가지고 올 데이터 수를 넣어준다.
        //  -> URL경로에 페이지 번호가 있으면 해당 페이지를, 페이지 번호가 없으면 0페이지를 조회하도록 설정한다.
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0,3);

        //조회 조건과 페이징 정보를 파라미터로 넘겨서 Page<Item> 객체를 리턴받는다.
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);

        //조회한 상품 데이터 및 페이징 정보를 뷰에 전달한다.
        model.addAttribute("items", items);

        //페이지 전환 시 기존 검색 조건을 유지한 채 이동할 수 있도록 뷰에 다시 전달한다.
        model.addAttribute("itemSearchDto", itemSearchDto);

        //상품 관리 메뉴 하단에 보여줄 페이지 번호의 최대 개수
        model.addAttribute("maxPage", 5);

        return "item/itemMng";
    }
}
