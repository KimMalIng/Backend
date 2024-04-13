package com.example.areyoup.member.dto;

import com.example.areyoup.member.domain.ProfileImage;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MemberResponseDto {

    @Getter
    @Setter
    @Builder
    public static class MemberJoinDto{
        private String memberId;
        private String name; //닉네임이 있을 경우
        private ProfileImage image;
        private String loginType;
    }
}
