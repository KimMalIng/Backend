package com.example.areyoup.job.repository;

import com.example.areyoup.job.domain.DefaultJob;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DefaultJobRepository extends JpaRepository<DefaultJob, Long> {

    @NonNull
    List<DefaultJob> findAllByMemberId(Long memberId);
}
