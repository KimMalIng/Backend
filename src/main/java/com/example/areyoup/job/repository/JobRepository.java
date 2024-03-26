package com.example.areyoup.job.repository;

import com.example.areyoup.job.domain.BasicJob;
import com.example.areyoup.job.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findJobsByMemberId(Long memberId);

}