package com.example.areyoup.profileimage.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileImageRequestDto {
    private Long id;
    private byte[] data;
}
