package com.example.areyoup.member.domain;

import com.example.areyoup.global.entity.BaseEntity;
import com.example.areyoup.member.dto.MemberResponseDto;
import com.example.areyoup.member.profileimage.domain.ProfileImage;
import com.example.areyoup.member.profileimage.dto.ProfileImageResponseDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    private String memberId; //이메일
    @NonNull
    private String memberPw;
    @NonNull
    private String name;
    private String nickname;
    private String loginType;

    //에브리타임 id pw
    private String everyTimeId;
    private String everyTimePw;

    private String refreshToken;
    private String fcmToken;

    @Builder.Default
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(
            name = "roles",
            joinColumns = @JoinColumn(name = "member_id")
    )
    private List<String> roles = new ArrayList<>();

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private ProfileImage profileImg;

    public void toUpdateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public MemberResponseDto.MemberJoinDto toDto(Member member){
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
