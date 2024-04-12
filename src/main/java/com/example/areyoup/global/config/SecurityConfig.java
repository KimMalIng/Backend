package com.example.areyoup.global.config;

import com.example.areyoup.global.cookie.CookieAuthorizationRequestRepository;
import com.example.areyoup.global.jwt.JwtAuthFilter;
import com.example.areyoup.global.oAuth2.OAuth2SuccessHandler;
import com.example.areyoup.global.oAuth2.OAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final OAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler successHandler;
    private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;

    private final JwtAuthFilter jwtAuthFilter;

    //    private final LoginFilter loginFilter;
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.httpBasic(HttpBasicConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                //REST API 이므로 basic auth 및 csrf 보안을 사용하지 않음
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(config -> config.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //JWT를 사용하기 때문에 세션을 사용하지 않음
                .authorizeHttpRequests(request ->
                                //요청에 대한 인가 규칙 설정
                                request
                                        .requestMatchers(
                                                new AntPathRequestMatcher("/oauth2/**"),
                                                new AntPathRequestMatcher("/login/oauth2/code/**"),
                                                new AntPathRequestMatcher("/users/login"),
                                                new AntPathRequestMatcher("/users/join")
                                        ).permitAll()
                                        //해당 API에 대해서는 모든 요청을 허가
                                        .anyRequest().authenticated()
                        //이 밖에 모든 요청에 대해서 인증 필요
                )
                .logout(logout -> logout
                        .logoutUrl("/users/logout")
                        .deleteCookies("JSESSIONID", "remember-me")
                        .addLogoutHandler(new LogoutHandler() {
                            @Override
                            public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
                                HttpSession session = request.getSession();
                                session.invalidate();
                            }
                        })
                        .logoutSuccessHandler(new LogoutSuccessHandler() {
                            @Override
                            public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
                                System.out.println("로그아웃");
                                response.sendRedirect("http://localhost:3000");
                            }
                        }))
                .oauth2Login(
                        oauth2 -> oauth2
                                .authorizationEndpoint(auth -> auth.authorizationRequestRepository(cookieAuthorizationRequestRepository))
                                .successHandler(successHandler)
                                //인증 성공시 처리하는 핸들러
                                .userInfoEndpoint(config -> config.userService(oAuth2UserService))
                        //oauth2 로그인 성공 후 설정 시작
                        //사용자 정보 객체를 가져오고 가져왔을 때 어떤 Service  파일을 쓸 것이냐
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//                .addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class);
        //JWT 인증을 위하여 직접 구현한 필터를 UsernamePasswordAuthenticationFilter 전에 실행

        return http.build();
    }


    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}



