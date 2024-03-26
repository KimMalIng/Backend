package com.example.areyoup.member;

import com.example.areyoup.global.BaseEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId; //이메일
    private String userPw;
    private String name;
    private String nickname;
    private String university;
    private String major;

    //에브리타임 id pw
    private String everyTimeId;
    private String everyTimePw;

    private String refreshToken;

    @ElementCollection(fetch=FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private ProfileImage profileImg;

//    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER)
//    private List<TimeTable> timeTables = new ArrayList<>();

}
