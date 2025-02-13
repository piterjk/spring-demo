package com.piterjk.springbootdemo.login.api;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.stream.Collectors;

record JwtRespose(String token){}

@RestController
public class LoginAPI {

    private final JwtEncoder jwtEncoder;
    private final AuthenticationManager authenticationManager;

    public LoginAPI(JwtEncoder jwtEncoder, AuthenticationManager authenticationManager) {
        this.jwtEncoder = jwtEncoder;
        this.authenticationManager = authenticationManager;
    }


    @PostMapping("/authenticate")
    public JwtRespose authentication(Authentication authentication) {
        return new JwtRespose(createToken(authentication));
    }

    @GetMapping("/api/data-load")
    public String dataLoad() {
        LocalDateTime now = LocalDateTime.now();
        return "OAuth 인증 처리 후 데이터 반환 처리 되었습니다." + now.toString();
    }


    @PostMapping("/auth/login")
    public JwtRespose authLogin(@RequestBody HashMap<String,Object> params) {
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        System.out.println("username: " + username + " password: " + password);
        // 사용자가 입력한 username과 password로 Authentication 객체 생성
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        return new JwtRespose(createToken(authentication));
    }

    private String createToken(Authentication authentication) {
        var claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .subject(authentication.getName())
                .claim("scope", createScope(authentication))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private String createScope(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(a->a.getAuthority())
                .collect(Collectors.joining(" "));
    }

}

