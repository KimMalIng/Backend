package com.example.areyoup.member.dto;

import com.example.areyoup.member.domain.Member;
import com.example.areyoup.member.domain.ProfileImage;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.web.multipart.MultipartFile;

@Data
@SuperBuilder
public class MemberResponseDto {

    protected String memberId;
    protected String name; //닉네임이 있을 경우
    protected ProfileImageDto image;
    protected String loginType;

    public MemberResponseDto(String memberId, String name, ProfileImageDto image, String loginType) {
        this.memberId = memberId;
        this.name = name;
        this.image = image;
        this.loginType = loginType;
    }

    @SuperBuilder
    public static class MemberJoinDto extends MemberResponseDto{

    }

    @SuperBuilder
    public static class MemberLoginDto extends MemberResponseDto {
        private final String accessToken;

        public MemberLoginDto(String memberId, String name, ProfileImageDto profileImageDto, String loginType, String accessToken) {
            super(memberId, name, profileImageDto, loginType);
            this.accessToken = accessToken;
        }

        public static MemberLoginDto toLoginDto(Member member, String accessToken) {
            return new MemberLoginDto(member.getMemberId(), member.getName(),
                    new ProfileImageDto(member.getProfileImg().getId(), member.getProfileImg().getData()),
                    member.getLoginType(), accessToken);
        }
    }
}
