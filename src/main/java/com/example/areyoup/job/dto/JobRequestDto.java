package com.example.areyoup.job.dto;

import com.example.areyoup.global.function.CalTime;
import com.example.areyoup.global.function.DateTimeHandler;
import com.example.areyoup.job.domain.CustomizeJob;
import com.example.areyoup.job.domain.DefaultJob;
import com.example.areyoup.job.domain.Job;
import com.example.areyoup.member.domain.Member;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.example.areyoup.global.function.CalTime.cal_estimatedTime;

@Data
public class JobRequestDto {
    private Long id;
    private String day;
    private String startTime;
    private String endTime;
    private Integer label;
    private String name;
    private String deadline;
    private String estimatedTime;

    private boolean isComplete;
    private boolean isFixed;

    @Data
    public static class PeriodRequestDto{
        private String startDate;
        private String endDate;
    }

    @Data
    public static class BaseDto{
        protected String name;
        protected Integer label;
        protected String startDate;
        protected String endDate;
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class FixedJobRequestDto extends  BaseDto{
        private String startTime;
        private String endTime;
        private boolean shouldClear;

        public static CustomizeJob toEntity(JobRequestDto.FixedJobRequestDto fixedJob, Member m){
            String estimatedTime = "";
            if (fixedJob.isShouldClear()){
                String startTime = fixedJob.getStartTime();
                LocalDateTime start = LocalDateTime.of(2024, 5,22,Integer.parseInt(startTime.split(":")[0]), Integer.parseInt(startTime.split(":")[1]));
                LocalDateTime end = LocalDateTime.of(2024,5,23,0,0);
                Duration duration = Duration.between(start, end);
                long hours = duration.toHours();
                long minutes = duration.toMinutes() % 60;

                estimatedTime = String.format("%02d:%02d", hours, minutes);
            } else{
                estimatedTime = CalTime.cal_Time(fixedJob.getStartTime(), fixedJob.getEndTime());
            }
            return CustomizeJob.builder()
                    .name(fixedJob.getName())
                    .label(fixedJob.getLabel())
                    .startDate(DateTimeHandler.strToDate(fixedJob.getStartDate()))
                    .deadline(fixedJob.getEndDate())
                    .estimatedTime(estimatedTime)
                    .isComplete(false)
                    .isFixed(true)
                    .member(m)
                    .completion(-1)
                    .startTime(fixedJob.getStartTime())
                    .endTime(fixedJob.getEndTime())
                    .shouldClear(fixedJob.isShouldClear())
                    .build();
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class AdjustJobRequestDto extends BaseDto{
        private String estimatedTime;

        public static CustomizeJob toEntity(AdjustJobRequestDto adjustJob, Member m) {
            LocalDate dl = DateTimeHandler.strToDate(adjustJob.getEndDate()).plusDays(1);
            LocalDateTime deadlineTime = dl.atStartOfDay();
            String deadline = deadlineTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));

            return CustomizeJob.builder()
                    .name(adjustJob.getName())
                    .label(adjustJob.getLabel())
                    .startDate(DateTimeHandler.strToDate(adjustJob.getStartDate()))
                    .deadline(deadline)
                    .estimatedTime(adjustJob.getEstimatedTime())
                    .isComplete(false)
                    .isFixed(false)
                    .member(m)
                    .completion(DateTimeHandler.strToTime(adjustJob.getEstimatedTime()).toSecondOfDay()/60)
                    .build();
        }
    }

    @Getter
    @Setter
    public static class UpdateJobRequestDto extends JobRequestDto{
        private String startDate;
        private String endDate;
        private Integer completion;
        private Integer dayOfTheWeek;
    }

    @Getter
    @Setter
    public static class DefaultJobRequestDto{
        private String name;
        private String startTime;
        private String endTime;

        public Job toJobEntity(JobRequestDto.DefaultJobRequestDto jobRequestDto, Member member){
            return DefaultJob.builder()
                    .name(jobRequestDto.getName())
                    .startTime(jobRequestDto.getStartTime())
                    .endTime(jobRequestDto.getEndTime())
                    .estimatedTime(cal_estimatedTime(jobRequestDto.getStartTime(), jobRequestDto.getEndTime()))
                    .member(member)
                    .label(0)
                    .isFixed(true)
                    .build();
        }
    }


}
