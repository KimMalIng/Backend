package com.example.areyoup.member.profileimage.repository;

import com.example.areyoup.member.profileimage.domain.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
}
