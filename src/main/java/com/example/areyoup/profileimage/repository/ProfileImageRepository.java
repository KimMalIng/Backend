package com.example.areyoup.profileimage.repository;

import com.example.areyoup.profileimage.domain.ProfileImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileImageRepository extends JpaRepository<ProfileImage, Long> {
}
