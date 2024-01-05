package com.capstone.AreyouP.Service;

import com.capstone.AreyouP.Domain.Token;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;

@Service
@Component
public class TokenService {
    private String secretKey = "token-secret-key";

    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public Token generateToken(String uid, String role){
        long tokenPeriod = 1000L*60L*10L;
        long refreshPeriod = 1000L*60L*60L*24L*30L*3L;

        Claims claims = Jwts.claims().setSubject(uid);
        claims.put("role", role);

        Date now = new Date();
        return new Token(
                "accessToken",
                "refreshToken");

    }

    public boolean verifyToken(String token){
        try{
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            return claimsJws.getBody()
                    .getExpiration()
                    .after(new Date());
        } catch(Exception e){
            return false;
        }
    }

    public String getUid(String token){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }
}
