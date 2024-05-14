package com.example.areyoup.member.dto;

import com.example.areyoup.member.domain.Member;
import com.example.areyoup.member.profileimage.dto.ProfileImageResponseDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class MemberResponseDto {

    protected String memberId;
    protected String name; //닉네임이 있을 경우
    protected String nickname;
    protected ProfileImageResponseDto image;
    protected String loginType;

    @SuperBuilder
    public static class MemberJoinDto extends MemberResponseDto{

        public static MemberJoinDto toDto(Member member){
            ProfileImageResponseDto profileImageResponseDto =
                    new ProfileImageResponseDto(member.getProfileImg().getId(),
                            ProfileImageResponseDto.convertByteArrayToBase64(member.getProfileImg().getData()));
            return MemberResponseDto.MemberJoinDto.builder()
                    .memberId(member.getMemberId())
                    .name(member.getName())
                    .nickname(member.getNickname())
                    .image(profileImageResponseDto)
                    .loginType(member.getLoginType())
                    .build();
        }

    }

    @SuperBuilder
    @Getter
    @Setter
    public static class MemberLoginDto extends MemberResponseDto {
        private String accessToken;

        public static MemberLoginDto toLoginDto(Member member, String accessToken) {
            return MemberLoginDto.builder()
                    .memberId(member.getMemberId())
                    .nickname(member.getNickname())
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

    @Builder
    @Getter
    public static class MemberUpdateDto{
        private String memberId;
        private String name;
        private String nickname;
        private String loginType;
        private String memberPw;

        public static MemberUpdateDto toDto(Member m) {
            return MemberUpdateDto.builder()
                    .memberId(m.getMemberId())
                    .memberPw(m.getMemberPw())
                    .name(m.getName())
                    .nickname(m.getNickname())
                    .loginType(m.getLoginType())
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class MemberImageUpdateDto{
        private ProfileImageResponseDto image;

        public static MemberImageUpdateDto toDto(Member m) {
            ProfileImageResponseDto profileImageResponseDto = new ProfileImageResponseDto(
                    m.getProfileImg().getId(),
                    ProfileImageResponseDto.convertByteArrayToBase64(m.getProfileImg().getData())
            );

            return new MemberImageUpdateDto(profileImageResponseDto);

        }
    }
}
