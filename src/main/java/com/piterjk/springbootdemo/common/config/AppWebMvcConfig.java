package com.piterjk.springbootdemo.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
public class AppWebMvcConfig implements WebMvcConfigurer {

    @Bean
    public InternalResourceViewResolver jspViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/"); // ✅ JSP 파일 위치
        resolver.setSuffix(".jsp"); // ✅ JSP 파일 확장자
        resolver.setViewClass(JstlView.class); // ✅ JSTL 지원 활성화
        return resolver;
    }
}
