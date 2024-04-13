package com.example.areyoup.profileimage.domain;

import com.example.areyoup.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ProfileImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private byte[] data;

    public void toUpdateDate(byte [] data){
        this.data = data;
    }
}
