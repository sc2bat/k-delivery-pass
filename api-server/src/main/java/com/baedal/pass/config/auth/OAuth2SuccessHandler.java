package com.baedal.pass.config.auth;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.baedal.pass.config.jwt.JwtTokenProvider;
import com.baedal.pass.domain.User;
import com.baedal.pass.mapper.UserMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final UserMapper userMapper;
    
    @Value("${app.front-url}")
    private String frontUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 구글의 식별자(sub)로 DB에서 유저 찾기
        String providerId = String.valueOf(attributes.get("sub")); 
        String provider = "google"; // 현재는 구글 고정 (추후 apple facebook 추가)

        User user = userMapper.findByProvider(provider, providerId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // JWT 토큰 생성
        String token = tokenProvider.createToken(String.valueOf(user.getUserId()), user.getRole());
        
        log.info("로그인 성공! 토큰 발급: {}", token);

        // 프론트엔드로 리다이렉트 (쿼리 파라미터로 토큰 전달)
        String targetUrl = UriComponentsBuilder.fromUriString(frontUrl + "/login-success") 
                .queryParam("token", token)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}