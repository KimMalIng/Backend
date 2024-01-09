package com.capstone.AreyouP.oAuth2;

import com.capstone.AreyouP.Configuration.Cookie.CookieAuthorizationRequestRepository;
import com.capstone.AreyouP.Configuration.Cookie.CookieUtils;
import com.capstone.AreyouP.DTO.JwtTokenDto;
import com.capstone.AreyouP.Domain.Member.Member;
import com.capstone.AreyouP.Repository.MemberRepository;
import com.capstone.AreyouP.Service.TokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static com.capstone.AreyouP.Configuration.Cookie.CookieAuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenService tokenService;
    private final MemberRepository memberRepository;
    private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        log.info("Principal에서 꺼낸 OAuth2User = {}", oAuth2User);

        String targetUrl;

        log.info("토큰 발행 시작");

        JwtTokenDto jwtTokenDto = processOAuth2User(oAuth2User); //회원가입 및 로그인

        CookieUtils.addCookie(response, "refreshToken" , jwtTokenDto.getRefreshToken(), 180);

        targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/logincheck") //
                .queryParam("accessToken",jwtTokenDto.getAccessToken())
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    //이 부분이 추가되면 된다
    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME).map(Cookie::getValue);
        //이미 OAuth2LoginAuthenticationFilter에서 authentication을 꺼내왔고 위에서 redirectUrl을 받아왔으므로 쿠키의 값은 제거하면 된다
        clearAuthenticationAttributes(request, response);
        return redirectUri.orElse(getDefaultTargetUrl());
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        cookieAuthorizationRequestRepository.removeAuthorizationRequestCookies(request,response);
    }


    private JwtTokenDto processOAuth2User(OAuth2User oAuth2User) throws JSONException {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String id = (String) attributes.get("id");
        String name = (String) attributes.get("name");
        String pw = passwordEncoder.encode(generateRandomString());

        JwtTokenDto jwtTokenDto;
        Optional<Member> userOptional = memberRepository.findByUserId(id);
        if (userOptional.isEmpty()){
            Member member = Member.builder()
                    .userId(id)
                    .userPw(pw)
                    .name(name)
                    .roles("ROLE_USER")
                    .build();

            memberRepository.save(member);
            log.info("회원가입 완료");
            jwtTokenDto = tokenService.signIn(id, pw);

        } else {
            jwtTokenDto = tokenService.signIn(id, userOptional.get().getPassword());
        }
        log.info("request id = {}, password = {}", id, pw);
        log.info("Auth2 JWT : accessToken = {}, refreshToken = {}",
                jwtTokenDto.getAccessToken(), jwtTokenDto.getRefreshToken());

        return jwtTokenDto;

    }

    private static String generateRandomString() {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder randomString = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            randomString.append(randomChar);
        }

        return randomString.toString();
    }


}

