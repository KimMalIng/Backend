package com.example.areyoup.job.repository;

import com.example.areyoup.job.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findJobsByMemberId(Long memberId);
    @Query(value = "SELECT SUM(TIME_TO_SEC(s.estimated_time) DIV 60) FROM Job s " +
            "WHERE s.dtype = 'S' GROUP BY s.name = :name" , nativeQuery = true)
    Integer getTotalEstimatedTimeByName(@Param("name") String name);
}