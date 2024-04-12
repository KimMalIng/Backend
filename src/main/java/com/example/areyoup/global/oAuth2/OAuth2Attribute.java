package com.example.areyoup.global.oAuth2;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString
@Builder(access = AccessLevel.PRIVATE)
@Getter
public class OAuth2Attribute {
    // provider마다 제공해주는 정보 값들이 다르기 때문에, 분기처리를 위해서 구현한 클래스
    private Map<String, Object> attributes;
    private String attributeKey;
    private String email;
    private String name;
    private String picture;

    public static OAuth2Attribute of(String provider, String attributeKey,
                                     Map<String, Object> attributes){
        if (provider.equals("kakao")) {
            return ofKaKao(attributeKey, attributes);
        }
        throw new RuntimeException();
    }

    private static OAuth2Attribute ofKaKao(String attributeKey, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuth2Attribute.builder()
                .name((String) kakaoProfile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .picture((String) kakaoProfile.get("profile_image_url"))
                .attributes(kakaoAccount)
                .attributeKey(attributeKey)
                .build();
    }

    public Map<String, Object> convertToMap(){
        Map<String,Object> map = new HashMap<>();
        map.put("id", attributeKey);
        map.put("name", name);
        map.put("email", email);
        map.put("picture", picture);

        return map;
    }

}
