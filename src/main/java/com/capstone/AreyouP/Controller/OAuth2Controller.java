//package com.capstone.AreyouP.Controller;
//
//import com.capstone.AreyouP.oAuth2.OAuth2UserService;
//import lombok.RequiredArgsConstructor;
//import org.apache.coyote.Response;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//
//@Controller
//@RequiredArgsConstructor
//public class OAuth2Controller {
//
//    @GetMapping("/login/oauth2/callback/kakao")
//    public ResponseEntity<?> OAuth2Login(){
//        return ResponseEntity.status(HttpStatus.OK).body(OAuth2UserService.save(OAuth2UserRequest));
//
//    }
//}
