package com.example.areyoup.member;

import com.example.areyoup.global.BaseEntity;
import jakarta.persistence.*;
import org.springframework.cglib.core.Local;

import java.time.LocalTime;
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

    //에브리타임 id pw
    private String everyTimeId;
    private String everyTimePw;

    private LocalTime startSleep;
    private LocalTime endSleep;

    private LocalTime startLunch;
    private LocalTime endLunch;

    private LocalTime startDinner;
    private LocalTime endDinner;
    private String refreshToken;

    @ElementCollection(fetch=FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private ProfileImage profileImg;

}
