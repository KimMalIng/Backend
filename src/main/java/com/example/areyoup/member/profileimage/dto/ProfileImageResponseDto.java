package com.example.areyoup.member.profileimage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Base64;

@Data
@AllArgsConstructor
public class ProfileImageResponseDto {
    private Long id;
    private String imageDataBase64;

    public static String convertByteArrayToBase64(byte[] byteArray) {
        return Base64.getEncoder().encodeToString(byteArray);
    }
}
