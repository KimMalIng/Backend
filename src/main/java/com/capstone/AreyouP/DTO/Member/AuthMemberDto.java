package com.capstone.AreyouP.DTO.Member;

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
