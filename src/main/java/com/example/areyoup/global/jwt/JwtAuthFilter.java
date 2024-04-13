package com.example.areyoup.global.jwt;

import com.example.areyoup.errors.errorcode.MemberErrorCode;
import com.example.areyoup.errors.exception.MemberException;
import com.example.areyoup.global.jwt.dto.AuthMemberDto;
import com.example.areyoup.global.jwt.dto.JwtTokenDto;
import com.example.areyoup.member.domain.Member;
import com.example.areyoup.member.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    /*클라이언트 요청 시 JWT 인증을 하기 위해 설치하는 커스텀 필터
    UsernamePasswordAuthenticationFilter 이전에 실행할 것

    클라이언트로부터 들어오는 요청에서 JWT 토큰을 처리하고, 유효한 토큰인 경우 해당 토큰의 인증 정보를
    SecurityContext에 저장하여 인증된 요청을 처리할 수 있도록 한다.

    즉 JWT를 통해 memberId + memberPassword 인증을 수행한다는 것*/

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        /*
        /users/login
        /users/join
        의 경우에는 현재 필터를 통과한다.
        아직 로그인이 안됐기 때문에 헤더를 확인할 필요 x
         */

        if (request.getRequestURL().equals("/users/login") ||
        request.getRequestURL().equals("/users/join")){
            filterChain.doFilter(request,response);
            return;
        }

        /*
        헤더에서 RefreshToken 추출
        존재 -> AccessToken이 만료되었다는 뜻
        존재x or 유효x -> null
        */
        String refreshToken = jwtTokenProvider.extractRefreshToken(request)
                .filter(jwtTokenProvider::validateToken)
                .orElse(null);

        /*
        RefreshToken != null (존재한다) 이라면 AccessToken 만료!
        RefreshToken이 DB와 일치하는지 확인하여 RefreshToken, AccessToken 재발급
        헤더에 Access 넣어서 다시 보내기, Refresh는 DB 저장
         */
        if (refreshToken!=null){
            Member member = memberRepository.findByRefreshToken(refreshToken)
                    .orElseThrow(() -> new MemberException(MemberErrorCode.REFRESHTOKEN_ERROR));
            AuthMemberDto authMemberDto = new AuthMemberDto(member.getMemberId(), member.getMemberPw(), member.getName());
            JwtTokenDto jwt = reCreateAccessTokenAndRefreshToken(authMemberDto); //여기서 Refresh DB 저장도 진행
            jwtTokenProvider.sendAccessToken(response, jwt.getAccessToken());
        }

        /*
        RefreshToken == null 이라면 AccessToken 존재
         */
        else {
            //헤더에서 토큰 추출 후 유효성 확인
            String token = jwtTokenProvider.extractAccessToken(request);
            if (token != null && jwtTokenProvider.validateToken(token)) { //유효성 검증
                Authentication auth = jwtTokenProvider.getAuthentication(token); //유저 정보 꺼내기
                SecurityContextHolder.getContext().setAuthentication(auth);
                //유효하면 Security Context에 저장 -> 요청을 처리하는 동안 인증 정보 유지
            }
            filterChain.doFilter(request, response); //다음 필터로 요청을 전달
        }
    }

    /*
    * RefreshToken 재발급 및 DB 업데이트
    * AccessToken 재발급
    * */
    public JwtTokenDto reCreateAccessTokenAndRefreshToken(AuthMemberDto user){
        Member m = memberRepository.findByMemberId(user.getMemberId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        JwtTokenDto jwtTokenDto = tokenService.signIn(user.getMemberId(), user.getMemberPw());
        m.toUpdateRefreshToken(jwtTokenDto.getRefreshToken());
        memberRepository.save(m);
        return jwtTokenDto;
    }



}
