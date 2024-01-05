package com.capstone.AreyouP.Domain;

import com.capstone.AreyouP.Repository.ProfileImageRepository;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Data
@NoArgsConstructor
@Entity
public class User {
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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ProfileImage profileImg;

    @OneToMany(mappedBy = "user")
    private List<TimeTable> timeTables = new ArrayList<>();

    @Builder
    public User(String userId, String userPw,
                String name, String nickname,
                String University, String major, ProfileImage profileImg) {
        this.userId = userId;
        this.userPw = userPw;
        this.name = name;
        this.nickname = nickname;
        this.university = University;
        this.major = major;
        this.profileImg = profileImg;
    }




}
