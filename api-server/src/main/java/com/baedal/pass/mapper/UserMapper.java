package com.baedal.pass.mapper;

import com.baedal.pass.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Optional;

@Mapper
public interface UserMapper {

    // 소셜 로그인 가입 여부 확인
    Optional<User> findByProvider(@Param("provider") String provider, @Param("providerId") String providerId);

    // 회원가입
    void save(User user);

    // 사용자 정보 수정
    void update(User user);
    
    // JWT 토큰 검증
    Optional<User> findById(Long userId);
}