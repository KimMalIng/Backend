package com.capstone.AreyouP.member;

import com.capstone.AreyouP.member.dto.MemberDto;
import com.capstone.AreyouP.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/join")
    public ResponseEntity<?> join(@ModelAttribute MemberDto memberDto) throws IOException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(memberService.create(memberDto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody MemberDto memberDto) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(memberService.login(memberDto));
    }


}
