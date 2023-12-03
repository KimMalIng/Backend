package com.capstone.AreyouP.Repository;

import com.capstone.AreyouP.Domain.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    @Query("SELECT c FROM Calendar  c WHERE c.Date =:date")
    Calendar findByDate(@Param("date") Date date);

}
