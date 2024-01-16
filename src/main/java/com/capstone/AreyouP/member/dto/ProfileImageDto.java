package com.capstone.AreyouP.member.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProfileImageDto {
    public String userId;
    public MultipartFile Image;
}
