package com.shop.service;

import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {
    
    @Value("${itemImgLocation}")
    private String itemImgLocation;
    
    private final ItemImgRepository itemImgRepository;
    
    private final FileService fileService;
    
    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception{
        //이미지파일의 원래 이름
        String oriImgName = itemImgFile.getOriginalFilename();
        
        //업로드 결과 로컬에 저장된 이미지파일의 이름
        String imgName = "";
        
        //로컬에 저장된 이미지파일을 불러올 경로
        String imgUrl = "";
        
        //사용자가 상품의 이미지를 등록했다면(oriImgName이 존재한다면)
        if(!StringUtils.isEmpty(oriImgName)){
            //이미지를 저장할 경로와 이미지의 원래 이름, 이미지의 바이트 배열을 파라미터로 하는 uploadFile() 메소드를 호출
            //-> 호출 결과 로컬에 저장돈 이미지위 이름을 imgName 변수에 저장
            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            
            //저장한 상품 이미지를 불러올 경로 설정
            imgUrl = "/images/item/" + imgName;
        }
        
        //입력받은 상품 이미지 정보를 저장
        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        itemImgRepository.save(itemImg);
    }

    public void updateItemImg(Long itemId, MultipartFile itemImgFile) throws Exception{
        //상품이미지를 수 정한 경우 상품 이미지를 업데이트
        if(!itemImgFile.isEmpty()){
            //상품 아이디를 이용하여 기존에 저장했던 상품 엔티티 조회
            ItemImg savedItemImg = itemImgRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
            
            //기존에 등록한 상품 이미지가 있을 경우 해당 파일을 삭제
            if(!StringUtils.isEmpty(savedItemImg.getImgName())){
                fileService.deleteFile(itemImgLocation + "/" + savedItemImg.getImgName());
            }

            String oriImgName = itemImgFile.getOriginalFilename();  //상품 이미지 파일의 원래 이름
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes()); //업데이트한 상품 이미지 파일을 업로드
            String imgUrl = "/images/item" + imgName; //상품 이미지 파일의 URL

            //변경된 상품 이미지 정보를 세팅
            //  -> 이때 상품 이미지를 등록하는 경우처럼 itemImgRepository.save() 로직을 호출하지 않고 savedItemImg의 updateItemImg() 로직을 호출한다.
            //  -> savedItemImg 엔티티는 현재 영속상태이므로 데이터를 변경하는 것 만으로도 변경 감지 기능이 작동하여
            //  -> 트랜잭션이 끝날 때 update 쿼리가 실행되기 때문이다.(반드시 엔티티가 영속상태이여야 한다.)
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
        }
    }
}
