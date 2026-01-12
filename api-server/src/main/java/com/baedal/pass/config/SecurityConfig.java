package com.baedal.pass.config;

import com.baedal.pass.config.auth.OAuth2SuccessHandler;
import com.baedal.pass.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF, CORS 등 API 서버에 불필요한 설정 끄기
            .csrf(csrf -> csrf.disable())
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable()) // 일반 로그인 폼 끄기
            
            // 세션 끄기 (JWT 사용)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // URL별 권한 관리
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/v1/stores/**",       // 가게 목록/상세
                    "/api/v1/categories/**",   // 카테고리
                    "/swagger-ui/**",          // 스웨거 문서
                    "/v3/api-docs/**",         // 스웨거 JSON
                    "/login-success",          // 로그인 성공 시
                    "/error",
                    "/images/**",
                    "/favicon.ico"
                ).permitAll()
                .anyRequest().authenticated()
            )

            // 소셜 로그인 설정 연결
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> 
                    userInfo.userService(customOAuth2UserService) // 유저 정보 받아오기
                )
                .successHandler(oAuth2SuccessHandler) // 로그인 성공 시 JWT 발급
            );

        return http.build();
    }
}