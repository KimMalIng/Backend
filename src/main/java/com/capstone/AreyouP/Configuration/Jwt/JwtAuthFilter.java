package com.capstone.AreyouP.Configuration.Jwt;

import com.capstone.AreyouP.Configuration.Cookie.CookieUtils;
import com.capstone.AreyouP.Domain.Member.Member;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthFilter extends GenericFilterBean {
    /*클라이언트 요청 시 JWT 인증을 하기 위해 설치하는 커스텀 필터
    UsernamePasswordAuthenticationFilter 이전에 실행할 것

    클라이언트로부터 들어오는 요청에서 JWT 토큰을 처리하고, 유효한 토큰인 경우 해당 토큰의 인증 정보를
    SecurityContext에 저장하여 인증된 요청을 처리할 수 있도록 한다.

    즉 JWT를 통해 username + password 인증을 수행한다는 것*/

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = resolveToken((HttpServletRequest) request);
        if (token != null && jwtTokenProvider.validateToken(token)){ //유효성 검증
            Authentication auth = jwtTokenProvider.getAuthentication(token); //유저 정보 꺼내기
            SecurityContextHolder.getContext().setAuthentication(auth);
            //유효하면 Security Context에 저장 -> 요청을 처리하는 동안 인증 정보 유지
        }
        chain.doFilter(request,response); //다음 필터로 요청을 전달
    }

    //JWT 토큰을 추출
    private String resolveToken(HttpServletRequest request) {
        log.info("CHECKT JWT : {}",request.getHeader("Authorization"));

        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")){
            return bearerToken.substring(7);
            //"Authorization"헤더에서 "Bearer" 접두사로 시작하는 토큰을 추출하여 반환
        }
        return null;
    }

}
