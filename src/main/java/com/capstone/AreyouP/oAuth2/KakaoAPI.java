package com.capstone.AreyouP.oAuth2;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

@Slf4j
@Component
public class KakaoAPI {
    private final String AUTHORIZATION = "Authorization";
    private final String BEARER_PREFIX = "Bearer ";

    private final String logoutURL = "https://kapi.kakao.com/v1/user/logout";

    public void logout(String accessToken) {
        try {
            URL url = new URL(logoutURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty(AUTHORIZATION, BEARER_PREFIX + accessToken);

            conn.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
