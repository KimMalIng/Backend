package com.capstone.AreyouP.DTO;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String userId;
    private String userPw;
    private String name;
    private String nickname;
    private String University;
    private String major;
}
