package com.capstone.AreyouP.Domain.Member;

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
