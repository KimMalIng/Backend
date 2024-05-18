package com.example.areyoup.job.repository;

import com.example.areyoup.job.domain.CustomizeJob;
import com.example.areyoup.job.domain.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    @Query(value = "SELECT SUM(TIME_TO_SEC(s.estimated_time) DIV 60) FROM job s " +
            "WHERE s.dtype = 'S' AND s.member_id = :id GROUP BY s.name = :name" , nativeQuery = true)
    Integer getTotalEstimatedTimeOfSeperatedJobByName(@Param("name") String name, @Param("id") Long memberId);

    @Query(value = "SELECT SUM(TIME_TO_SEC(s.estimated_time) DIV 60) FROM job s " +
            "WHERE s.dtype = 'S' " +
            "AND s.member_id = :id " +
            "AND s.is_complete = false " +
            "AND s.name = :name" , nativeQuery = true)
    Integer getTotalEstimatedTimeOfSeperatedJobByNameAndIsCompleteFalse(@Param("name") String name, @Param("id") Long memberId);


    @Query(value = "SELECT SUM(TIME_TO_SEC(s.estimated_time) DIV 60) FROM job s " +
            "WHERE s.dtype = 'D' AND s.member_id = :id ", nativeQuery = true)
    Integer getLeftTimeFromDefaultJob(@Param("id") Long memberId);

    @Query(value = "SELECT * FROM job cj WHERE cj.dtype='C' AND " +
            "cj.start_time IS NULL AND cj.member_id = :id " +
            "AND cj.is_complete = false AND DATE_FORMAT(cj.deadline, '%Y-%m-%d') >= :startDate",
            nativeQuery = true)
    List<CustomizeJob> findAdjustJobWhichIsDeadLineRemain(@Param("id") Long memberId, @Param("startDate") LocalDate startDate);


    @Query(value = "SELECT j.dtype FROM job j WHERE j.id = :id", nativeQuery = true)
    String getDtypeFromJob(@Param("id") Long jobId);


    void deleteAllByMemberId(Long memberId);

    @Query(value = "SELECT * FROM job j WHERE j.day = :day AND j.start_time = :startTime", nativeQuery = true)
    List<Job> findAllByDayAndStartTimeEquals(@Param("day") LocalDate day, @Param("startTime") String startTime);

    @Query(value = "SELECT * FROM job j WHERE j.day = :day AND j.end_time = :endTime", nativeQuery = true)

    List<Job> findAllByDayAndEndTimeEquals(@Param("day") LocalDate day, @Param("endTime")String endTime);


}