package com.baedal.pass.service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baedal.pass.domain.User;
import com.baedal.pass.mapper.UserMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserMapper userMapper;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerId = String.valueOf(attributes.get("sub")); // 구글 고유 ID
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        log.info("Social Login Request: {} / {}", registrationId, email);

        // 사용자 정보 업데이트
        User user = saveOrUpdate(registrationId, providerId, email, name);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole())),
                attributes,
                userNameAttributeName
        );
    }

    private User saveOrUpdate(String provider, String providerId, String email, String nickname) {
        Optional<User> optionalUser = userMapper.findByProvider(provider, providerId);

        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setNickname(nickname);
            userMapper.update(existingUser);
            return existingUser;
        } else {
            User newUser = User.builder()
                    .email(email)
                    .nickname(nickname)
                    .provider(provider)
                    .providerId(providerId)
                    .role("ROLE_USER")
                    .build();
            userMapper.save(newUser);
            return newUser;
        }
    }
}