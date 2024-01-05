package com.capstone.AreyouP.oAuth2.user;
import com.capstone.AreyouP.oAuth2.exception.OAuth2ProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class OAuth2UserUnlinkManager {

    private final KakaoOAuth2UserUnlink kakaoOAuth2UserUnlink;


    public void unlink(OAuth2Provider provider, String accessToken) {
        if (OAuth2Provider.KAKAO.equals(provider)) {
            kakaoOAuth2UserUnlink.unlink(accessToken);
        } else {
            throw new OAuth2ProcessingException(
                    "Unlink with " + provider.getRegistrationId() + " is not supported");
        }
    }
}