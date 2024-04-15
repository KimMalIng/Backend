package com.example.areyoup.mvc.service;

import com.example.areyoup.member.profileimage.domain.ProfileImage;
import com.example.areyoup.member.profileimage.dto.ProfileImageResponseDto;
import com.example.areyoup.member.profileimage.repository.ProfileImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MvcService {
    @Autowired
    ProfileImageRepository profileImageRepository;
    public List<ProfileImageResponseDto> getProfile() {
        List<ProfileImage> profileImageList = profileImageRepository.findAll();
        List<ProfileImageResponseDto> profileImageDtos = new ArrayList<>();
        for (ProfileImage profileImage : profileImageList) {
            String imageDataBase64 = ProfileImageResponseDto.convertByteArrayToBase64(profileImage.getData());
            ProfileImageResponseDto profileImageDto = new ProfileImageResponseDto(
                    profileImage.getId(),
                    imageDataBase64);
                    // 다른 필드 설정
            profileImageDtos.add(profileImageDto);
        }

        return profileImageDtos;
    }


}
