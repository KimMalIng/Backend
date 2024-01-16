package com.capstone.AreyouP.global.jwt;

import com.capstone.AreyouP.member.repository.MemberRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    public static final long ACCESSTOKEN_TIME = 1000*60*30;
    public static final long REFRESHTOKEN_TIME = 1000*60*60*24*7;
    private final Key key;

    //application.yml에서 secret 값을 가져와서 key 에 저장
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey,
                            MemberRepository memberRepository){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    //인증된 객체(authentication)를 가지고 AccessToken, RefreshToken 생성
    public JwtTokenDto generateToken(Authentication authentication){
       String authorities = authentication.getAuthorities().stream()
               .map(GrantedAuthority::getAuthority)
               .collect(Collectors.joining(","));
       //권한 가져오기

       long now = (new Date()).getTime();

       Date accessTokenExpiresIn = new Date(now+ACCESSTOKEN_TIME
       //        5000

       );
       String accessToken = Jwts.builder()
               .setSubject(authentication.getName())
               .claim("auth",authorities)
               .setExpiration(accessTokenExpiresIn)
               .signWith(key, SignatureAlgorithm.HS256)
               .compact();

       String refreshToken = Jwts.builder()
               .setExpiration(new Date(now+REFRESHTOKEN_TIME))
               .signWith(key, SignatureAlgorithm.HS256)
               .compact();

       return JwtTokenDto.builder()
               .grantType("Bearer")
               .accessToken(accessToken)
               .refreshToken(refreshToken)
               .build();
    }

    //refreshToken 요청이 왔을 때 accessToken만 생성
    public String generateAccessToken(Authentication authentication){
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();

        Date accessTokenExpiresIn = new Date(now+ACCESSTOKEN_TIME);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth",authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //JWT 토큰을 복호화하여 토큰에 있는 사용자의 인증 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken){
        //토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get("auth") == null){
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        //클레임에서 권한 정보 가져오기
        //토큰의 Claims에서 권한 정보를 추출하고, User 객체를 생성하여 Authentication 객체로 변환
        //Collection<? extends GrantedAuthority>로 리턴 받아서 다양한 타입의 객체로 처리, 더 큰 유연성과 확장성
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        //토큰의 클레임에서 권한 정보 가져오기, auth 클레임은 권한 정보를 나타냄
                        .map(SimpleGrantedAuthority::new)
                        //가져온 권한 정보를 SimpleGrantedAuthority  객체로 변환하여 컬렉션에 추가
                        .collect(Collectors.toList());


        //UserDetails 객체를 만들어서 Authentication 리턴
        //UserDetails  - interface
        //User - UserDetails를 구현한 class
        UserDetails principle = new User(claims.getSubject(), "", authorities);
        //claims.getSubject()는 주어진 토큰의 클레임에서 "sub" 클레임의 값을 반환 , 토큰의 주체를 나타냄 (사용자의 식별자나 이메일 주소)


        //UsernameP~~ 객체 생성하여 주체와 권한 정보를 포함한 인증 객체 생성
        return new UsernamePasswordAuthenticationToken(principle, "", authorities);
    }

    public Long getExpiration(String accessToken){
        Date expiration = Jwts.parserBuilder().setSigningKey(key)
                .build().parseClaimsJws(accessToken).getBody().getExpiration();

        long now = new Date().getTime();
        return expiration.getTime() - now;
    }

    //토큰 유효성 검사
    public boolean validateToken(String token){
        try{
            //토큰의 서명 키를 설정하고, 예외처리를 통해 토큰의 유효성 여부 판단
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch(SecurityException | MalformedJwtException e){
            log.info("Invalid JWT token", e);
            e.getMessage();
        } catch(ExpiredJwtException e){
            log.info("Expired JWT Token", e);
            e.getMessage();
        } catch(UnsupportedJwtException e){
            log.info("Unsupported JWT Token", e);
            e.getMessage();
        } catch(IllegalArgumentException e){
            //토큰이 올바른 형식이 아니거나 claim이 비어있는 경우
            log.info("JWT claims string is empty", e);
            e.getMessage();
        }
        return false;
    }

    //클레임(Claims) : 토큰에서 사용할 정보의 조각
    // 주어진 Access token을 복호화, 만료된 토큰인 경우에는 Claims 반환
    //parseClaimsJws() 메서드가 JWT 토큰의 검증과 파싱을 모두 수행
    private Claims parseClaims(String accessToken){
        try{
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch(ExpiredJwtException e){
            return e.getClaims();
        }
    }

    /*
    * AccessToken 헤더에 실어서 보내기
    */
    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("accessToken", accessToken);
        log.info("재발급된 Access Token : {}", accessToken);
    }

    //Access + Refresh 헤더에 실어서 보내기
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken){
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader("accessToken", accessToken);
        response.setHeader("refreshToken", refreshToken);
        log.info("Access Token, Refresh Token 헤더 설정 완료");
    }

    //JWT 토큰을 추출
    public String extractAccessToken(HttpServletRequest request) {
        log.info("CHECK JWT : {}",request.getHeader("Authorization"));

        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")){
            return bearerToken.substring(7);
            //"accessToken"헤더에서 "Bearer" 접두사로 시작하는 Access 토큰을 추출하여 반환
        }
        return null;
    }

//    //헤더에서 AccessToken 추출 (위와 비슷?)
//    public Optional<String> extractAccessToken (HttpServletRequest request){
//        return Optional.ofNullable(request.getHeader("accessToken"))
//                .filter(refresh -> refresh.startsWith("Bearer"))
//                .map(refresh -> refresh.replace("Bearer", ""));
//    }

    //헤더에서 RefreshToken 추출
    public Optional<String> extractRefreshToken (HttpServletRequest request){
        return Optional.ofNullable(request.getHeader("refreshToken"))
                .filter(refresh -> refresh.startsWith("Bearer "))
                .map(refresh -> refresh.replace("Bearer ", ""));
    }

    //AccessToken에서 userId 추출
    public String extractUserId(String accessToken){
        try{
            Boolean validation = validateToken(accessToken); //유효성 검사
            if (validation){
                Claims claims = parseClaims(accessToken); //토큰에서 정보 가져오기
                return claims.getSubject(); //사용자 id를 subject에 넣어놨기에 가져오기!
            }
        } catch (Exception e){
            log.info("엑세스 토큰이 유효하지 않습니다.");
        }
        return null;
    }

}
