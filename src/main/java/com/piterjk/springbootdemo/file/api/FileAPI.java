package com.piterjk.springbootdemo.file.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
public class FileAPI {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping("/file/uploadFile")
    public String handleFileUpload(@RequestParam("files") MultipartFile[] files) {
        if (files.length == 0 ) {
            return "파일이 선택되지 않았습니다.";
        }

        StringBuffer uploadedFiles = new StringBuffer();

        // 여러 파일 처리
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            // 파일명 추출 후 UUID로 고유한 파일명 생성
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension;

            // 파일 저장 경로 설정
            try {
                String filePath = Paths.get(uploadDir, uniqueFileName).toString();
                File dest = new File(filePath);
                file.transferTo(dest);
                uploadedFiles.append(uniqueFileName).append(" ");
            } catch (IOException e) {
                return "파일 업로드 실패: " + e.getMessage();
            }
        }
        return "파일 업로드 성공: " + uploadedFiles.toString();
    }
}
