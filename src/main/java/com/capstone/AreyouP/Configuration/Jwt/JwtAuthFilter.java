package com.capstone.AreyouP.Configuration.Jwt;

import com.capstone.AreyouP.Domain.User;
import com.capstone.AreyouP.Repository.UserRepository;
import com.capstone.AreyouP.Service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends GenericFilterBean {
    private final TokenService tokenService;
    private final UserRepository userRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = ((HttpServletRequest) request).getHeader("Auth");

        if (token != null && tokenService.verifyToken(token)){
            String email = tokenService.getUid(token);

            User user = User.builder()
                    .userId(email)
                    .name("이름")
                    .build();

            userRepository.save(user);
            Authentication auth = getAuthentication(user);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }

    public Authentication getAuthentication(User user){
        return new UsernamePasswordAuthenticationToken(user, "",
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
