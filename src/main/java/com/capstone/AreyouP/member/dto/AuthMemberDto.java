package com.capstone.AreyouP.member.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthMemberDto {
    private String userId;
    private String userPw;
    private String name;
}
