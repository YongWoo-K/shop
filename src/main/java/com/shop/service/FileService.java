package com.shop.service;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
@Log
public class FileService {
    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception{
        //UUID는 파일구별을 위해 사용
        UUID uuid = UUID.randomUUID();
        
        //파일의 확장자
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        
        //UUID로 받은 값과 파일의 확장자를 조합하여 저장할 파일의 이름을 생성
        String savedFileName = uuid.toString() + extension;
        
        //파일을 업로드(저장)할 경로
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;
       
       //생성자로 파일이 저장될 위치와 파일의 이름을 넘겨 파일에 쓸 파일 출력 스트림을 생성
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
        
        //fileData를 파일 출력 스트림에 입력
        fos.write(fileData);
        fos.close();
        
        //업로드된 파일의 이름을 리턴
        return savedFileName;
    }

    public void deleteFile(String filePath) throws Exception{
        //파일 저장 경로를 이용하여 파일 객체 생성
        File deleteFile = new File(filePath);
        
        //해당 파일 존재시 파일 삭제
        if(deleteFile.exists()){
            deleteFile.delete();
            log.info("파일을 삭제하였습니다.");
        } else{
            log.info("파일이 존재하지 않습니다.");
        }
    }
}
