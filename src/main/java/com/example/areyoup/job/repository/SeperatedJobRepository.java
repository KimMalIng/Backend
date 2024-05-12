package com.example.areyoup.job.repository;

import com.example.areyoup.job.domain.SeperatedJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SeperatedJobRepository extends JpaRepository<SeperatedJob, Long> {
    List<SeperatedJob> findByDayBetweenAndIsFixedIsTrueAndMemberId(LocalDate start, LocalDate end, Long memberId);

    SeperatedJob findByDayAndStartTimeAndMemberId(LocalDate day, String startTime, Long memberId);


    @Transactional
    void deleteAllByDayAfterAndNameAndMemberId(LocalDate day, String name, Long memberId);

    @Transactional
    void deleteAllByDayAfterAndNameAndIsCompleteIsFalse(LocalDate now, String name);
}
