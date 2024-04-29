package com.example.areyoup.global.oAuth2;


import com.example.areyoup.global.cookie.CookieAuthorizationRequestRepository;
import com.example.areyoup.global.cookie.CookieUtils;
import com.example.areyoup.global.jwt.TokenService;
import com.example.areyoup.global.jwt.dto.JwtTokenDto;
import com.example.areyoup.member.domain.Member;
import com.example.areyoup.member.profileimage.domain.ProfileImage;
import com.example.areyoup.member.repository.MemberRepository;
import com.example.areyoup.member.profileimage.repository.ProfileImageRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static java.lang.System.getenv;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;
    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;

    private final MemberRepository memberRepository;
    private final ProfileImageRepository profileImageRepository;



    Map<String, String> env = getenv();
    String host = env.get("HOST");
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        log.info("OAuth2User = {}", oAuth2User);
        log.info("토큰 발행 시작");

        JwtTokenDto jwtTokenDto = processOAuth2User(oAuth2User); //회원가입 및 로그인
//        CookieUtils.addCookie(response, "refreshToken" , jwtTokenDto.getRefreshToken(), 180);

        String targetUrl = UriComponentsBuilder.fromUriString("http://"+ host +":3000/logincheck")
                .queryParam("accessToken",jwtTokenDto.getAccessToken())
                .queryParam("refreshToken", jwtTokenDto.getRefreshToken())
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    //이 부분이 추가되면 된다
    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtils.getCookie(request, CookieAuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME).map(Cookie::getValue);
        //이미 OAuth2LoginAuthenticationFilter에서 authentication을 꺼내왔고 위에서 redirectUrl을 받아왔으므로 쿠키의 값은 제거하면 된다
        clearAuthenticationAttributes(request, response);
        return redirectUri.orElse(getDefaultTargetUrl());
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        cookieAuthorizationRequestRepository.removeAuthorizationRequestCookies(request,response);
    }



    public JwtTokenDto processOAuth2User(OAuth2User oAuth2User) throws JSONException, IOException {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String pw = passwordEncoder.encode(generateRandomString());

        JwtTokenDto jwtTokenDto;
        Optional<Member> userOptional = memberRepository.findByMemberId(email);
        String picUrl = (String) oAuth2User.getAttributes().get("picture");
        ProfileImage profileImage = getProfileImage(picUrl);
        profileImageRepository.save(profileImage);
        if (userOptional.isEmpty()){
            Member member = Member.builder()
                    .memberId(email)
                    .memberPw(pw)
                    .name(name)
                    .roles(Collections.singletonList("USER"))
                    .profileImg(profileImage)
                    .loginType("Web-kakao")
                    .build();
            memberRepository.save(member);

            jwtTokenDto = tokenService.signIn(email, pw);
            member.toUpdateRefreshToken(jwtTokenDto.getRefreshToken());
            log.info("OAuth2 Join Success");
        } else {
            jwtTokenDto = tokenService.signIn(email, userOptional.get().getMemberPw());
            //token 발행
        }
        log.info("OAuth2 request id = {}", email);
        log.info("OAuth2 JWT success");
        return jwtTokenDto;

    }

    public static ProfileImage getProfileImage(String picUrl) throws IOException {
        URL urlInput = new URL(picUrl);
        BufferedImage urlImage = ImageIO.read(urlInput);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(urlImage, "jpg", bos);
        byte[] file = bos.toByteArray();

        ProfileImage profileImage = new ProfileImage();
        profileImage.toUpdateData(file);
        return profileImage;
    }

    public static String generateRandomString() {
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

