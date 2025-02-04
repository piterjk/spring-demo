package com.piterjk.springbootdemo.common.component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class AppAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

        // ✅ AJAX 요청인지 확인 (JSON 요청이면 JSON 응답 반환)
        String ajaxHeader = request.getHeader("X-Requested-With");
        boolean isAjaxRequest = "XMLHttpRequest".equals(ajaxHeader);

        if (isAjaxRequest || request.getRequestURI().startsWith("/api/")) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            PrintWriter writer = response.getWriter();
            writer.write("{\"error\": \"Forbidden\", \"message\": \"권한이 없습니다.\"}");
            writer.flush();
        } else {
            // ✅ 일반 요청이면 접근 거부 페이지로 이동
            response.sendRedirect("/access-denied");
        }
    }
}
