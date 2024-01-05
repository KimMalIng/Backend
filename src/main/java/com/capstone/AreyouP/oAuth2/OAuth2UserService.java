package com.capstone.AreyouP.oAuth2;

import com.capstone.AreyouP.oAuth2.OAuth2Attribute;
import com.capstone.AreyouP.Domain.User;
import com.capstone.AreyouP.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService{
    //정상적인 유저 인증이 완료되면 여기로 오게 된다.
    //그 다음에 successhandler로 감

    private final UserRepository userRepository;

//    public static OAuth2User save(OAuth2UserRequest userRequest) {
//
//    }

    //OAuth2User에는 개인정보 요청이 들어있음
    //아래 메소드를 바탕으로 인증 처리
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
        OAuth2User oAuth2User = super.loadUser(userRequest);
        //생성된 Service 객체로 부터 User를 받는다.

        //받은 User로 부터 user 정보를 받는다.
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                        .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        log.info("registrationId = {}",registrationId);
        log.info("userNameAttributeName = {}",userNameAttributeName);

        //SuccessHandler가 사용할 수 있도록 등록해준다.
        OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        var memberAttribute = oAuth2Attribute.convertToMap();

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                memberAttribute, "email");


    }

    private void processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) throws JSONException {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        System.out.println("here" + attributes);

        Map<String, Object> properties = (Map<String, Object>) attributes.get("kakao_account");
        String email = (String) properties.get("email");

        System.out.println("email" + email);
        Optional<User> userOptional = userRepository.findByUserId(email);
        if (userOptional.isPresent()){
            return;
        }
        registerUser(userRequest, properties);

    }

    private void registerUser(OAuth2UserRequest oAuth2UserRequest, Map<String, Object> oAuth2User){
        Map<String, Object> nickname =  (Map<String, Object>) oAuth2User.get("properties");

        User user = User.builder()
                .userId((String) oAuth2User.get("email"))
                .userPw("abc")
                .name(nickname.toString())
                .build();

        userRepository.save(user);
    }


}
