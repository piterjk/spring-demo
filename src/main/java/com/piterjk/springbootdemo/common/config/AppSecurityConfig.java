package com.piterjk.springbootdemo.common.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.piterjk.springbootdemo.common.component.AppAccessDeniedHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.model.AclService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import java.util.UUID;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class AppSecurityConfig {

    private final org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource;

    public AppSecurityConfig(org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtDecoder jwtDecoder) throws Exception {

        return http.addFilterBefore(corsFilter(), CorsFilter.class) // CORS 필터 추가
                .authorizeHttpRequests(auth->{
                    auth.requestMatchers(
                                    "/assets/**",
                                    "/auth/**",
                                    "/login",
                                    "/error",
                                    "/access-denied",
                                    "/WEB-INF/views/**").permitAll()
                        .requestMatchers("/").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/authenticate/**").hasRole("USER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated();
            })
            .sessionManagement(session->{
                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
            })//세션 사용하지 않는다.
            //.httpBasic(Customizer.withDefaults()) // HTTP Basic 인증 활성화
            .formLogin(from->from
                    .loginPage("/login")
                    .failureUrl("/login?error=true") // 로그인 실패 시 이동할 URL
                    .defaultSuccessUrl("/", true)
                    .permitAll()
            )
            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout=true")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .permitAll()
            )
            .csrf(AbstractHttpConfigurer::disable)// CSRF 보호 비활성화 (API 서버의 경우)
            .headers(headers ->{
                    headers.frameOptions(frameOptions -> frameOptions.sameOrigin()) // X-Frame-Options 설정
                            .contentSecurityPolicy(
                                    csp -> csp.policyDirectives("default-src 'self'; style-src 'self' 'nonce-3e54b9e6-40c0-425f-ac88-8a34dd0adc46'; script-src 'self' 'nonce-3e54b9e6-40c0-425f-ac88-8a34dd0adc46'")); // CSP 설정)
                    }
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder))) // JWT 방식 적용);
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(new CustomAuthenticationEntryPoint()) // 401 Unauthorized 처리
                    .accessDeniedHandler(new AppAccessDeniedHandler())
            )
            .build();

    }

    @Bean
    public CorsFilter corsFilter() {
        // CORS 설정을 위한 CorsConfiguration 설정
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000")); // 허용할 출처
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
        corsConfiguration.setAllowedHeaders(List.of("*")); // 모든 헤더 허용
        corsConfiguration.setAllowCredentials(true); // 쿠키 허용

        // URL에 대한 CorsConfiguration을 등록
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration); // 모든 경로에 대해 CORS 허용

        return new CorsFilter(source); // CorsFilter를 반환
    }

    // ✅ AuthenticationManager를 Bean으로 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

//    @Bean
//    WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**") // 모든 경로에 대해 CORS 허용
//                        .allowedOrigins("http://localhost:3000") // 클라이언트 출처 허용
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
//                        .allowedHeaders("*") // 모든 헤더 허용
//                        //.allowedHeaders("Content-Type", "Authorization")
//                        .allowCredentials(true); // 인증 정보 허용
//            }
//        };
//    }

    public static class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException authException) throws IOException {

            // AJAX 또는 API 요청이면 JSON 응답 반환
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With")) || request.getRequestURI().startsWith("/api/")) {
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                PrintWriter writer = response.getWriter();
                writer.write("{\"error\": \"Unauthorized\", \"message\": \"로그인이 필요합니다.\"}");
                writer.flush();
            } else {
                // 일반 요청이면 로그인 페이지로 리디렉션
                response.sendRedirect("/login");
            }
        }
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        System.out.println(passwordEncoder().encode("piterjk"));
//        var user = User.withUsername("piterjk")
//                .password(passwordEncoder().encode("piterjk"))
//                .roles("USER")
//                .build();
//        return new InMemoryUserDetailsManager(user);
//    }

    @Bean
    public KeyPair keyPair() {
        KeyPairGenerator keyParGenerator = null;
        try {
            keyParGenerator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keyParGenerator.initialize(2048);
        return keyParGenerator.generateKeyPair();
    }

    @Bean
    public RSAKey rsaKey(KeyPair keyPair) {
        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey(keyPair.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    @Bean
    public JWKSource jwkSource(RSAKey rsaKey) {
        var jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext)->jwkSelector.select(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(RSAKey rsaKey) throws JOSEException {
        return NimbusJwtDecoder
                .withPublicKey(rsaKey.toRSAPublicKey())
                .build();
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    // ✅ PasswordEncoder 빈 추가 (Spring Security에서 필수)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    //AclPermissionEvaluator
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(AclService aclService, AclAuthorizationStrategy aclAuthorizationStrategy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(new AclPermissionEvaluator(aclService));
        return expressionHandler;
    }


}
