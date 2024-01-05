package com.capstone.AreyouP.oAuth2.user;

import com.capstone.AreyouP.oAuth2.exception.OAuth2ProcessingException;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId,
                                                   String accessToken,
                                                   Map<String, Object> attributes) {
        if (OAuth2Provider.KAKAO.getRegistrationId().equals(registrationId)) {
            return new KakaoOAuth2UserInfo(accessToken, attributes);
        } else {
            throw new OAuth2ProcessingException("Login with " + registrationId + " is not supported");
        }
    }
}