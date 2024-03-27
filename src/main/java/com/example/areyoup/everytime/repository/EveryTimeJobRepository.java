package com.example.areyoup.everytime.repository;

import com.example.areyoup.everytime.domain.EveryTimeJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface EveryTimeJobRepository extends JpaRepository<EveryTimeJob, Long> {
    //dayOfWeek에 있는 요일에 해당 되는 EveryTimeJob 꺼내기
    List<EveryTimeJob> findByDayOfTheWeekIn(Set<Integer> dayOfTheWeek);

    List<EveryTimeJob> findByDayOfTheWeek(Integer dayOfTheWeek);
}
