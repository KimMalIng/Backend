package com.example.areyoup.global.oAuth2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService{
    //정상적인 유저 인증이 완료되면 여기로 오게 된다.
    //그 다음에 successhandler로 감

    //OAuth2User에는 개인정보 요청이 들어있음
    //아래 메소드를 바탕으로 인증 처리
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
        OAuth2User oAuth2User = super.loadUser(userRequest);
        //생성된 Service 객체로 부터 User를 받는다.

        //받은 User로 부터 user 정보를 받는다.
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String id = oAuth2User.getAttributes().get("id").toString();

        //SuccessHandler가 사용할 수 있도록 등록해준다.
        OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(registrationId, id, oAuth2User.getAttributes());

        var memberAttribute = oAuth2Attribute.convertToMap();

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                memberAttribute, "id");
    }






}
