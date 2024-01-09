package com.capstone.AreyouP.Configuration;

import com.capstone.AreyouP.Configuration.Cookie.CookieAuthorizationRequestRepository;
import com.capstone.AreyouP.Configuration.Jwt.JwtAuthFilter;
import com.capstone.AreyouP.oAuth2.OAuth2SuccessHandler;
import com.capstone.AreyouP.oAuth2.OAuth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
                                                new AntPathRequestMatcher("/login/oauth2/code/**")
                                        ).permitAll()
                                        //해당 API에 대해서는 모든 요청을 허가
                                        .anyRequest().authenticated()
                        //이 밖에 모든 요청에 대해서 인증 필요
                )
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

//    @Bean
//    @Override
//    public AuthenticationManager authenticationManager() throws Exception{
//        return super.authenticationManager();
//    }
//
//    @Bean
//    LoginFilter loginFilter() throws Exception{
//        LoginFilter loginFilter = new LoginFilter();
//        loginFilter.setAuthenticationManager(loginFilter().authenticationManager());
//    }


    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }



//
//    @Bean
//    public LoginFilter loginFilter() throws Exception{
//        LoginFilter filter = new LoginFilter();
//        filter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
//        return filter;
//    }
}
//
//import com.capstone.AreyouP.oAuth2.OAuth2UserService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.servlet.DispatcherType;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.MediaType;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.client.web.*;
//import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
//import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.AuthenticationFailureHandler;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
//import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
//
//import java.io.PrintWriter;
//import java.nio.charset.StandardCharsets;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//public class SecurityConfig {
//    private final OAuth2UserService oAuth2UserService;
//    private final ObjectMapper objectMapper;
//
//    @Bean
//    public SecurityFilterChain filterChain(
//            HttpSecurity http,
//            OAuth2AuthorizedClientRepository requestAuthorizationRequestRepository,
//            AuthenticationFailureHandler authenticationFailureHandler,
//            OAuth2UserService oAuth2UserService
//
//    ) throws Exception{
//        http
//                .csrf(AbstractHttpConfigurer::disable) // csrf 끄기
//                .authorizeHttpRequests(authorizeHttpRequests ->
//                        authorizeHttpRequests
//                                .requestMatchers(
//                                        new AntPathRequestMatcher("/oauth2/**"),
//                                        new AntPathRequestMatcher("/login/oauth2/code/**")
//                                ).permitAll()
//                                .anyRequest().authenticated()
//                )
//                .oauth2Login(oauth2 -> oauth2
//                        .authorizationEndpoint(authorizationEndpointConfig -> authorizationEndpointConfig
//                                .baseUri("/oauth2/authorization/**"))
//                        //해당 url로 접근시 로그인 요청
//                        .authorizedClientRepository(requestAuthorizationRequestRepository)
//                        .redirectionEndpoint(redirectionEndpointConfig -> redirectionEndpointConfig
//                                .baseUri("/login/oauth2/callback/**"))
//                        //callback 주소
//                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
//                        .successHandler(successHandler())
//                        .failureHandler(authenticationFailureHandler)
//                )
//                .exceptionHandling(exceptionHandling ->
//                        exceptionHandling
//                                .authenticationEntryPoint(
//                                        (httpServletRequest, httpServletResponse, e) -> httpServletResponse.sendError(401)
//                                )
//                                .accessDeniedHandler(
//                                        (httpServletRequest, httpServletResponse, e) -> httpServletResponse.sendError(403)
//                                )
//                )
////
//                .logout((logout) -> logout
//                        .logoutRequestMatcher(new AntPathRequestMatcher("/users/logout")) //로그아웃 URL 설정
//                        .logoutSuccessUrl("/") //로그아웃 성공 시
//                        .deleteCookies()
//                        .invalidateHttpSession(true));//생성된 사용자 세션 삭제
//
//        return http.build();
//    }
//
//    @Bean
//    public AuthenticationSuccessHandler successHandler(){
//        return((request, response, authentication) -> {
//            DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
//
//            String id = defaultOAuth2User.getAttributes().get("id").toString();
//            String body = """
//                    {"id":"%s"}
//                    """.formatted(id);
//            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
////            objectMapper.writeValue(response.getWriter(), defaultOAuth2User);
//
//            PrintWriter writer = response.getWriter();
//            writer.println(body);
//            writer.flush();
//        });
//    }
//
////    @Bean
////    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> httpSessionOAuth2AuthorizationRequestRepository() {
////        return new HttpSessionOAuth2AuthorizationRequestRepository();
////    }
//
//    @Bean
//    public OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository(){
//        return new HttpSessionOAuth2AuthorizedClientRepository();
//    }
//
//    @Bean
//    public AuthenticationFailureHandler authenticationFailureHandler() {
//        return new SimpleUrlAuthenticationFailureHandler();
//    }
//
////    @Bean
////    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
////        return new DefaultOAuth2UserService();
////    }
//
//
//
//}




