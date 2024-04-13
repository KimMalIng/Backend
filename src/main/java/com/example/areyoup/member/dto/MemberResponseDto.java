package com.example.areyoup.member.dto;

import com.example.areyoup.member.domain.Member;
import com.example.areyoup.profileimage.dto.ProfileImageRequestDto;
import com.example.areyoup.profileimage.dto.ProfileImageResponseDto;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class MemberResponseDto {

    protected String memberId;
    protected String name; //닉네임이 있을 경우
    protected ProfileImageResponseDto image;
    protected String loginType;

    @SuperBuilder
    public static class MemberJoinDto extends MemberResponseDto{

    }

    @SuperBuilder
    @Getter
    @Setter
    public static class MemberLoginDto extends MemberResponseDto {
        private String accessToken;

        public static MemberLoginDto toLoginDto(Member member, String accessToken) {
            return MemberLoginDto.builder()
                    .memberId(member.getMemberId())
                    .name(member.getName())
                    .image(new ProfileImageResponseDto(member.getProfileImg().getId(),
                            ProfileImageResponseDto.convertByteArrayToBase64(member.getProfileImg().getData())))
                    .loginType(member.getLoginType())
                    .accessToken(accessToken)
                    .build();
        }
    }

    
    @Getter
    @SuperBuilder
    public static class MemberInfoDto extends MemberResponseDto{
        private String memberPw;
        private String nickname;
        private String everyTimeId;
        private String everyTimePw;

        public static MemberInfoDto toInfoDto(Member m) {
            return getMemberInfoDto(m);
        }

        public static MemberInfoDto getMemberInfoDto(Member m) {
            ProfileImageResponseDto profileImageResponseDto = new ProfileImageResponseDto(
                    m.getProfileImg().getId(),
                    ProfileImageResponseDto.convertByteArrayToBase64(m.getProfileImg().getData())
            );
            return MemberInfoDto.builder()
                    .memberId(m.getMemberId())
                    .memberPw(m.getMemberPw())
                    .name(m.getName())
                    .everyTimeId(m.getEveryTimeId())
                    .everyTimePw(m.getEveryTimePw())
                    .image(profileImageResponseDto)
                    .nickname(m.getNickname())
                    .loginType(m.getLoginType())
                    .build();
        }


    }

}
