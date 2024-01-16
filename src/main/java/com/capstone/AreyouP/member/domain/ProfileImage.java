package com.capstone.AreyouP.member.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

@Entity
@Data
public class ProfileImage {

    @Id
    @GeneratedValue
    private Long id;

    @Lob
    private byte[] data;
}
