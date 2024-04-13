package com.example.areyoup.member.domain;

import com.example.areyoup.global.entity.BaseEntity;
import com.example.areyoup.member.dto.MemberResponseDto;
import com.example.areyoup.member.dto.ProfileImageDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

    //에브리타임 id pw
    private String everyTimeId;
    private String everyTimePw;
    private String refreshToken;

    private String loginType;

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
        ProfileImageDto profileImageDto =
                new ProfileImageDto(member.getProfileImg().getId(), member.getProfileImg().getData());
        return MemberResponseDto.MemberJoinDto.builder()
                .memberId(member.getMemberId())
                .name(member.getName())
                .image(profileImageDto)
                .loginType(member.getLoginType())
                .build();
    }

}
