package com.example.areyoup.job.repository;

import com.example.areyoup.job.domain.CustomizeJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CustomizeJobRepository extends JpaRepository<CustomizeJob, Long> {
    //start - end 사이의 일정 모두 반환
    List<CustomizeJob> findByStartDateBetweenAndIsFixedIsTrue(LocalDate start, LocalDate end);

    List<CustomizeJob> findByIsFixedIsTrue();

}
