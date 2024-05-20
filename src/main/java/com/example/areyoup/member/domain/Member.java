package com.example.areyoup.member.domain;

import com.example.areyoup.global.entity.BaseEntity;
import com.example.areyoup.fcm.domain.fcmEntity;
import com.example.areyoup.member.dto.MemberRequestDto;
import com.example.areyoup.member.profileimage.domain.ProfileImage;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
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

    @Builder.Default
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(
            name = "roles",
            joinColumns = @JoinColumn(name = "member_id")
    )
    private List<String> roles = new ArrayList<>();

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private ProfileImage profileImg;

    @OneToOne(cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    private fcmEntity fcm;

    public void toUpdateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void toUpdateAll(MemberRequestDto.MemberUpdateDto memberUpdateDto, PasswordEncoder passwordEncoder) throws IOException {
        if (!memberPw.equals(memberUpdateDto.getMemberPw())){
            this.memberPw = passwordEncoder.encode(memberUpdateDto.getMemberPw());
        }
        this.name = memberUpdateDto.getName();
        this.nickname = memberUpdateDto.getNickname();
    }

    public void toUpdateFcm(fcmEntity fcm){
        this.fcm = fcm;
    }
}
