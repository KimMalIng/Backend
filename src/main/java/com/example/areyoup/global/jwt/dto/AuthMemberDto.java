package com.example.areyoup.global.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
public class AuthMemberDto {
    private String memberId;
    private String memberPw;
    private String name;
}
