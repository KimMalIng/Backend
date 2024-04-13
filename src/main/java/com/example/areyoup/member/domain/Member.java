package com.example.areyoup.member.domain;

import com.example.areyoup.global.entity.BaseEntity;
import com.example.areyoup.member.dto.MemberResponseDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    private String memberId; //이메일
    private String memberPw;
    private String name;

    //에브리타임 id pw
    private String everyTimeId;
    private String everyTimePw;
    private String refreshToken;

    private String loginType;

    @ElementCollection(fetch=FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProfileImage profileImg;

    public void toUpdateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public MemberResponseDto.MemberJoinDto toDto(Member member){
        return MemberResponseDto.MemberJoinDto.builder()
                .memberId(member.getMemberId())
                .name(member.getName())
                .image(member.getProfileImg())
                .loginType(member.getLoginType())
                .build();

    }

}
