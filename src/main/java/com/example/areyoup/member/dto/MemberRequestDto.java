package com.example.areyoup.member.dto;

import jakarta.persistence.Basic;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

@Data
@SuperBuilder
public class MemberRequestDto {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberJoinDto{
        private String memberId;
        private String memberPw;
        private String name;
        private String nickname;
        private MultipartFile image;
    }
}
