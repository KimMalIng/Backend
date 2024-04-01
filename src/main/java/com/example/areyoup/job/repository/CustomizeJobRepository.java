package com.example.areyoup.job.repository;

import com.example.areyoup.job.domain.CustomizeJob;
import com.example.areyoup.job.domain.SeperatedJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CustomizeJobRepository extends JpaRepository<CustomizeJob, Long> {
    //start - end 사이의 일정 모두 반환
    @Query("SELECT cj FROM CustomizeJob cj WHERE cj.startDate BETWEEN :start AND :end AND cj.isFixed = true")
    List<CustomizeJob> findByStartDateBetweenAndIsFixedIsTrue(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Query("SELECT cj FROM CustomizeJob cj WHERE cj.startTime IS NULL AND cj.isFixed = false")
    List<CustomizeJob> findAdjustJob();

    @Query("SELECT cj FROM CustomizeJob cj WHERE cj.startTime IS NOT NULL " +
            "AND cj.startDate BETWEEN :start AND :end " +
            "AND cj.isFixed = true")
    List<CustomizeJob> findFixedJob(@Param("start") LocalDate start, @Param("end") LocalDate end);
    CustomizeJob findByName(String name);

    List<CustomizeJob> findAllByMemberId(Long memberId);


}
