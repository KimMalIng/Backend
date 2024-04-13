package com.example.areyoup.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileImageDto {
    private Long id;
    private byte[] data;
}
