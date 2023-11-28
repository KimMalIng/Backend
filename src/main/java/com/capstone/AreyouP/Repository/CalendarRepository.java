package com.capstone.AreyouP.Repository;

import com.capstone.AreyouP.Domain.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Date;

public interface CalendarRepository extends JpaRepository<Calendar, Date> {
}
