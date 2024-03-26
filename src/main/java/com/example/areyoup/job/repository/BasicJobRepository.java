package com.example.areyoup.job.repository;

import com.example.areyoup.job.domain.BasicJob;
import com.example.areyoup.job.dto.JobResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface BasicJobRepository extends JpaRepository<BasicJob, Long> {
    //dayOfWeek에 있는 요일에 해당 되는 BasicJob 꺼내기
    List<BasicJob> findByDayOfTheWeekIn(Set<Integer> dayOfTheWeek);
}
