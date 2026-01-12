package com.baedal.pass.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long userId;
    
    private String email;
    private String password;      // 소셜 로그인으로 null 
    private String nickname;
    private String phoneNumber;
    
    private String role;          // ROLE_USER, ROLE_ADMIN
    private String fcmToken;      // 푸시 알림 토큰
    
    private String provider;      // google, apple, facebook
    private String providerId;    // 소셜 서비스 식별값
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}