//package com.capstone.AreyouP.oAuth2;
//
//import com.capstone.AreyouP.DTO.UserDto;
//import com.capstone.AreyouP.Domain.Token;
//import com.capstone.AreyouP.Repository.UserRepository;
//import com.capstone.AreyouP.Service.TokenService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.io.IOException;
//
//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//    private final TokenService tokenService;
//    private final UserRepository userRequestMapper;
//    private final ObjectMapper objectMapper;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
//            throws IOException, ServletException {
//
//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//        UserDto userDto = userRequestMapper.toDto(oAuth2User);
//
//        log.info("Principal에서 꺼낸 OAuth2User = {}", oAuth2User);
//
//        String targetUrl;
//        log.info("토큰 발행 시작");
//
//        Token token = tokenService.generateToken(userDto.getUserId(), "USER");
//        log.info("{}", token);
//        targetUrl = UriComponentsBuilder.fromUriString("/home")
//                .queryParam("token", "token")
//                .build().toUriString();
//
//        getRedirectStrategy().sendRedirect(request, response, targetUrl);
//    }
//}