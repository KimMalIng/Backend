package com.capstone.AreyouP.Repository;

import com.capstone.AreyouP.Domain.TimeTable;
import com.capstone.AreyouP.Domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {

    @Query("SELECT t FROM TimeTable t WHERE t.calendar.Date BETWEEN :startDate AND :endDate")
    List<TimeTable> findAllByCalendarDate(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
