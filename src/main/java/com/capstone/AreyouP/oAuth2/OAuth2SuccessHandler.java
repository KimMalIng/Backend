package com.capstone.AreyouP.oAuth2;

import com.capstone.AreyouP.DTO.UserDto;
import com.capstone.AreyouP.Domain.Token;
import com.capstone.AreyouP.Repository.UserRepository;
import com.capstone.AreyouP.Service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    TokenService tokenService;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        log.info("Principal에서 꺼낸 OAuth2User = {}", oAuth2User);

        String targetUrl;

        log.info("토큰 발행 시작");

        Token token = tokenService.generateToken(oAuth2User.getAttributes().get("id").toString(), "USER");
        log.info("{}", token);

        targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/main")
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}

