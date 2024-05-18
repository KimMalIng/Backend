package com.example.areyoup.fcm.repository;

import com.example.areyoup.fcm.domain.fcmEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface fcmRepository extends JpaRepository<fcmEntity, Long> {
}
