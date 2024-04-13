package com.example.areyoup.profile.service;

import com.example.areyoup.profileimage.domain.ProfileImage;
import com.example.areyoup.profileimage.dto.ProfileImageResponseDto;
import com.example.areyoup.profileimage.repository.ProfileImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class ProfileService {
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
