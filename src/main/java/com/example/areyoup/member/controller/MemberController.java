package com.example.areyoup.member.controller;

import com.example.areyoup.member.dto.MemberRequestDto;
import com.example.areyoup.member.dto.MemberResponseDto;
import com.example.areyoup.member.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<MemberResponseDto.MemberJoinDto> join(@ModelAttribute MemberRequestDto.MemberJoinDto memberDto) throws IOException {
        return ResponseEntity.ok()
                .body(memberService.join(memberDto));
    }

    @PostMapping("/login")
    public ResponseEntity<MemberResponseDto.MemberLoginDto> login(HttpServletResponse response, @RequestBody MemberRequestDto.MemberLoginDto memberDto){
        return ResponseEntity.ok()
                .body(memberService.login(response, memberDto));
    }

}
