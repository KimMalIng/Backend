package com.capstone.AreyouP.oAuth2;

import com.capstone.AreyouP.oAuth2.OAuth2Attribute;
import com.capstone.AreyouP.Domain.User;
import com.capstone.AreyouP.Repository.UserRepository;
import jakarta.servlet.http.HttpSession;
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
    private final HttpSession httpSession;

    //OAuth2User에는 개인정보 요청이 들어있음
    //아래 메소드를 바탕으로 인증 처리
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException{
        OAuth2User oAuth2User = super.loadUser(userRequest);
        //생성된 Service 객체로 부터 User를 받는다.

        //받은 User로 부터 user 정보를 받는다.
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        log.info(oAuth2User.toString());

        String id = oAuth2User.getAttributes().get("id").toString();

        processOAuth2User(oAuth2User);//회원가입

        //SuccessHandler가 사용할 수 있도록 등록해준다.
        OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(registrationId, id, oAuth2User.getAttributes());

        var memberAttribute = oAuth2Attribute.convertToMap();

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                memberAttribute, "id");
    }

    private void processOAuth2User(OAuth2User oAuth2User) throws JSONException {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        Long id = (Long) attributes.get("id");
        String name = (String) properties.get("nickname");

        Optional<User> userOptional = userRepository.findByUserId(String.valueOf(id));
        if (userOptional.isEmpty()){
            User user = User.builder()
                    .userId(String.valueOf(id))
                    .name(name)
                    .build();

            userRepository.save(user);
        }

    }



}
