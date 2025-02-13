package com.piterjk.springbootdemo.file.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
public class FileAPI {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.allowed-extensions}")
    private String allowedExtensions;

    @PostMapping("/file/uploadFile")
    public String handleFileUpload(@RequestParam("files") MultipartFile[] files) {
        if (files.length == 0 ) {
            return "파일이 선택되지 않았습니다.";
        }

        List<String> uploadedFiles = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();
        List<String> rejectedFiles = new ArrayList<>();

        // 허용된 확장자 목록을 ',' 기준으로 분리하여 리스트로 변환
        List<String> allowedList = Arrays.asList(allowedExtensions.split(","));

        // 여러 파일 처리
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }

            String fileName = file.getOriginalFilename();
            if (fileName == null) continue;

            // 파일 확장자 추출
            String fileExtension = getFileExtension(fileName);

            // 허용된 확장자 확인
            if (!allowedList.contains(fileExtension.toLowerCase())) {
                rejectedFiles.add(fileName);
                continue;
            }

            // 파일명 추출 후 UUID로 고유한 파일명 생성
            String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension;

            // 파일 저장 경로 설정
            try {
                String filePath = Paths.get(uploadDir, uniqueFileName).toString();
                File dest = new File(filePath);
                file.transferTo(dest);
                uploadedFiles.add(fileName);
            } catch (IOException e) {
                failedFiles.add(fileName);
            }
        }

        StringBuilder sb = new StringBuilder();
        if (!uploadedFiles.isEmpty()) {
            sb.append("업로드 성공: ").append(String.join(", ", uploadedFiles));
        }
        if (!failedFiles.isEmpty()) {
            sb.append("업로드 실패: ").append(String.join(", ", failedFiles));
        }
        if (!rejectedFiles.isEmpty()) {
            sb.append("허용되지 않은 확장자: ").append(String.join(", ", rejectedFiles));
        }

        return sb.toString();
    }

    // 파일 확장자 추출 메서드
    private String getFileExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        if (lastIndex == -1) {
            return ""; // 확장자가 없는 경우
        }
        return fileName.substring(lastIndex + 1);
    }
}
