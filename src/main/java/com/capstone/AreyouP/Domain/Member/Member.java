package com.capstone.AreyouP.Domain.Member;

import com.capstone.AreyouP.Domain.TimeTable;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString
public class Member implements UserDetails {
    @Id
    @GeneratedValue
    private Long id;

    private String userId;
    //이메일
    private String userPw;
    private String name;
    private String nickname;
    private String university;
    private String major;

    @ElementCollection(fetch=FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProfileImage profileImg;

    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER)
    private List<TimeTable> timeTables = new ArrayList<>();

    @Builder
    public Member(String userId, String userPw,
                  String name, String nickname,
                  String University, String major, ProfileImage profileImg, String roles) {
        this.userId = userId;
        this.userPw = userPw;
        this.name = name;
        this.nickname = nickname;
        this.university = University;
        this.major = major;
        this.profileImg = profileImg;
        this.roles = Collections.singletonList(roles);
    }


    //유저가 가지고 있는 권한 목록을 SimpleGrantedAuthority로 변환하여 변환
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return userPw;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
