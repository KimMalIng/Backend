package com.capstone.AreyouP.global.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class TokenService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public JwtTokenDto signIn(String username, String password){
        //1. username(userId) + password(userPw) 를 기반으로 Authentication 객체 생성
        //이때 authentication은 인증 여부를 판단하는 aujthenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new
                UsernamePasswordAuthenticationToken(username, password);

        //실제 검증. authenticate() 메서드를 통해 요청된 user에 대한 검증 진행
        //authenticate 메서드가 실행될 때 CustomUserDetailsService에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication =
                authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        return jwtTokenProvider.generateToken(authentication);
    }

    public JwtTokenDto reissue(Authentication authentication){
        String token = jwtTokenProvider.generateAccessToken(authentication);
        JwtTokenDto jwtTokenDto = new JwtTokenDto();
        jwtTokenDto.setAccessToken(token);
        return jwtTokenDto;
    }

}
