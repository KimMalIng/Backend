package com.example.areyoup.job.dto;

import com.example.areyoup.job.domain.CustomizeJob;
import com.example.areyoup.job.service.JobService;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class JobRequestDto {
    private Long user_id;
    private Long job_id;
    private String day;
    private String startTime;
    private String endTime;
    private Integer label;
    private String name;
    private String deadline;
    private String estimated_time;

    private boolean isPrivate;
    private boolean isComplete;
    private boolean isFixed;

    @Data
    public static class PeriodRequestDto{
        private Long memberId;
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

        public static CustomizeJob toEntity(JobRequestDto.FixedJobRequestDto fixedJob){
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            LocalDate start = LocalDate.parse(fixedJob.getStartDate(), dtf);

            return CustomizeJob.builder()
                    .name(fixedJob.getName())
                    .label(fixedJob.getLabel())
                    .day(start)
                    .deadline(fixedJob.getEndDate())
                    .estimated_time(JobService.cal_Time(fixedJob.getStartTime(), fixedJob.getEndTime()))
                    .isComplete(false)
                    .isFixed(true)
//                    .completion(0)
                    .startTime(fixedJob.getStartTime())
                    .endTime(fixedJob.getEndTime())
                    .shouldClear(fixedJob.isShouldClear())
                    .build();
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class AdjustJobRequestDto extends BaseDto{
        private String estimated_time;

        public static CustomizeJob toEntity(AdjustJobRequestDto adjustJob) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            LocalDate start = LocalDate.parse(adjustJob.getStartDate(), dtf);

            LocalDate dl = LocalDate.parse(adjustJob.getEndDate(), dtf).plusDays(1);
            LocalDateTime deadlineTime = dl.atStartOfDay();
            String deadline = deadlineTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));

            return CustomizeJob.builder()
                    .name(adjustJob.getName())
                    .label(adjustJob.getLabel())
                    .day(start)
                    .deadline(deadline)
                    .estimated_time(adjustJob.getEstimated_time())
                    .isComplete(false)
                    .isFixed(false)
//                    .completion(0)
                    .build();
        }
    }


}
