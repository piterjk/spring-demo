package com.piterjk.springbootdemo.common.service;

import com.piterjk.springbootdemo.common.entity.CommonCode;
import com.piterjk.springbootdemo.common.repository.CommonCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommonCodeService {

    private final CommonCodeRepository commonCodeRepository;

    //최초 DB 조회 > 이후 캐시 조회
    @Cacheable(value = "commonCodes", key = "#code")
    public String getCommonCodeName(String code) {
        Optional<CommonCode> commonCode = commonCodeRepository.findByCode(code);
        return commonCode.map(CommonCode::getCodeName).orElse("NOT FOUND");
    }

    //새로운 코드 등록 및 캐시 업데이트
    @CachePut(value = "commonCodes", key = "#code")
    public String addCommonCode(String code, String name, String codeGroup) {
        CommonCode commonCode = new CommonCode();
        commonCode.setCode(code);
        commonCode.setCodeName(name);
        commonCode.setCodeGroup(codeGroup);
        commonCodeRepository.save(commonCode);
        return name;  // 캐시에도 즉시 업데이트
    }


    // 캐시 삭제
    @CacheEvict(value = "commonCodes", allEntries = true)  // 기존 캐시 삭제
    public void evictCommonCode(String code, String name) {
        System.out.println("모든 캐시 삭제됨");
    }

}
