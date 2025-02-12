package com.piterjk.springbootdemo.common.config;


import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class AppCacheConfig {

    @Bean
    public CacheManager cacheManager() {
        // 기본적으로 ConcurrentHashMap을 사용하는 간단한 캐시
        return new ConcurrentMapCacheManager("aclCache","commonCodes");
    }

}
