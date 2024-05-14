package com.example.areyoup.member.dto;

import jakarta.persistence.Basic;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class MemberRequestDto {


    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberJoinDto  {
        private String memberId;
        private String memberPw;
        private String name;
        private String nickname;
        private MultipartFile image;

    }


    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class MemberLoginDto {
        private String memberId;
        private String memberPw;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberUpdateDto {
        private String memberId;
        private String memberPw;
        private String name;
        private String nickname;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberImageUpdateDto {
        private MultipartFile image;
    }


}
