package com.capstone.AreyouP.timetable;

import com.capstone.AreyouP.job.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TimeTableRepository extends JpaRepository<TimeTable, Long> {

    @Query("SELECT t FROM TimeTable t WHERE t.calendar.Date BETWEEN :startDate AND :endDate")
    List<TimeTable> findAllByCalendarDate(@Param("startDate") Date startDate,
                                          @Param("endDate") Date endDate);

    @Query("SELECT Job FROM TimeTable t WHERE t.member.id=:userId")
    List<Job> findJobByUserId(@Param("userId") Long user_id);

    @Query("SELECT t FROM TimeTable t WHERE t.member.id =:userId AND t.calendar.Date BETWEEN :startDate AND :endDate")
    List<TimeTable> findAllByCalendarDateAndUserId(@Param("startDate") Date startDate,
                                                   @Param("endDate") Date endDate,
                                                   @Param("userId") Long user_id);

    @Query("SELECT t FROM TimeTable t WHERE t.member.id =:userId AND t.job.isComplete =false AND t.job.label != 0")
    List<TimeTable> findAllByUserIdAndJobIsNOTCompleteAndJobLabelISNOTZERO(@Param("userId") Long user_id);

}
