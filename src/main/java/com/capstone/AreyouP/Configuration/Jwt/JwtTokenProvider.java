package com.capstone.AreyouP.Configuration.Jwt;

import com.capstone.AreyouP.DTO.JwtTokenDto;
import com.capstone.AreyouP.Domain.Member.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;

    //application.yml에서 secret 값을 가져와서 key 에 저장
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey){
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

       Date accessTokenExpiresIn = new Date(now+86400000); //유효기간 1일, 보통 30분인데 테스트를 위해
       String accessToken = Jwts.builder()
               .setSubject(authentication.getName())
               .claim("auth",authorities)
               .setExpiration(accessTokenExpiresIn)
               .signWith(key, SignatureAlgorithm.HS256)
               .compact();

       String refreshToken = Jwts.builder()
               .setExpiration(new Date(now+8640000))
               .signWith(key, SignatureAlgorithm.HS256)
               .compact();

       return JwtTokenDto.builder()
               .grantType("Bearer")
               .accessToken(accessToken)
               .refreshToken(refreshToken)
               .build();
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
        } catch(ExpiredJwtException e){
            log.info("Expired JWT Token", e);
        } catch(UnsupportedJwtException e){
            log.info("Unsupported JWT Token", e);
        } catch(IllegalArgumentException e){
            //토큰이 올바른 형식이 아니거나 claim이 비어있는 경우
            log.info("JWT claims string is empty", e);
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

//    public String loginGenerateToken(Member m){
//        Map<String, Object> claims = new HashMap<>();
//        claims.put("loginId", m.getUserId());
//        claims.put("loginPw", m.getUserPw());
//        claims.put("id",m.getId());
//        return Jwts.builder()
//                .setHeaderParam("typ","JWT")
//                .setClaims(claims)
//                .setSubject("id")
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis()+1000L*60*60))
//                .signWith(SignatureAlgorithm.HS256, key)
//                .compact();
//    }



}
