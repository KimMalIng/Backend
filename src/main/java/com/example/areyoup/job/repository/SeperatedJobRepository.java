package com.example.areyoup.job.repository;

import com.example.areyoup.job.domain.SeperatedJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SeperatedJobRepository extends JpaRepository<SeperatedJob, Long> {
    List<SeperatedJob> findByDayBetweenAndIsFixedIsTrue(LocalDate start, LocalDate end);

    SeperatedJob findByDayAndStartTime(LocalDate day, String startTime);

}
