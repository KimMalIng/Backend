package com.example.areyoup.fcm.domain;

import com.example.areyoup.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder @NoArgsConstructor @AllArgsConstructor
public class fcmEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fcmToken;


    public void toUpdateFcmToken(String fcmToken){
        this.fcmToken = fcmToken;
    }
}
