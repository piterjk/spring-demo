package com.piterjk.springbootdemo.file.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

@Controller
public class FileController {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.allowed-extensions}")
    private String allowedExtensions;

    @RequestMapping("/file/upload")
    public String uploadFile() {
        return "file/upload";
    }

    @PostMapping("/file/upload")
    public String uploadFiles(@RequestParam("files") MultipartFile[] files, Model model) {
        if (files.length == 0) {
            model.addAttribute("message", "파일을 선택하세요!");
            return "file/upload";
        }

        List<String> uploadedFiles = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();
        List<String> rejectedFiles = new ArrayList<>();

        // 허용된 확장자 목록을 ',' 기준으로 분리하여 리스트로 변환
        List<String> allowedList = Arrays.asList(allowedExtensions.split(","));

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String fileName = file.getOriginalFilename();
                if (fileName == null) continue;

                // 파일 확장자 추출
                String fileExtension = getFileExtension(fileName);

                // 허용된 확장자 확인
                if (!allowedList.contains(fileExtension.toLowerCase())) {
                    rejectedFiles.add(fileName);
                    continue;
                }

                // 유니크한 파일명 생성 (UUID)
                String uniqueFileName = UUID.randomUUID().toString() + "." + fileExtension;

                try {
                    String filePath = Paths.get(uploadDir, uniqueFileName).toString();
                    File dest = new File(filePath);
                    file.transferTo(dest);
                    uploadedFiles.add(fileName);
                } catch (IOException e) {
                    failedFiles.add(fileName);
                }
            }
        }

        if (!uploadedFiles.isEmpty()) {
            model.addAttribute("message", "업로드 성공: " + String.join(", ", uploadedFiles));
        }
        if (!failedFiles.isEmpty()) {
            model.addAttribute("error", "업로드 실패: " + String.join(", ", failedFiles));
        }
        if (!rejectedFiles.isEmpty()) {
            model.addAttribute("reject", "허용되지 않은 확장자: " + String.join(", ", rejectedFiles));
        }

        return "file/upload";
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

