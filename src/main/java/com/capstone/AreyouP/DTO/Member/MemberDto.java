package com.capstone.AreyouP.DTO.Member;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private String userId;
    private String userPw;
    private String name;
    private String nickname;
    private String university;
    private String major;
    private MultipartFile image;

}
