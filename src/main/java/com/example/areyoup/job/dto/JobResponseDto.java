package com.example.areyoup.job.dto;

import com.example.areyoup.everytime.domain.EveryTimeJob;
import com.example.areyoup.global.function.DateTimeHandler;
import com.example.areyoup.job.domain.CustomizeJob;
import com.example.areyoup.job.domain.Job;
import com.example.areyoup.job.domain.SeperatedJob;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
@SuperBuilder
public class JobResponseDto {

//    private Long user_id;
//    private Long job_id;

    //Job
    private Long id;
    private String name;
    private Integer label;
    private String startTime;
    private String endTime;
    private String estimatedTime;
    private boolean isFixed;
//    private boolean isPrivate;
    private boolean isComplete;

    //EveryTimeJob
//    private String dayOfTheWeek;

    //CustomizeJob
//    private String day;
//    private String deadline;
//    private String completion;
//    private boolean isFixed;
//    private boolean shouldClear;

    public static JobResponseDto toDto(Job j) {
        return JobResponseDto.builder()
                .id(j.getId())
                .name(j.getName())
                .label(j.getLabel())
                .startTime(j.getStartTime())
                .endTime(j.getEndTime())
                .estimatedTime(j.getEstimatedTime())
                .isComplete(j.isComplete())
                .isFixed(j.isFixed())
                .build();
    }


    @Getter
    @SuperBuilder
    public static class FixedJobResponseDto extends JobResponseDto{
        private final String startDate;
        private final String deadline;
        private final boolean isFixed;
        private final boolean shouldClear;


        // CustomizeJob Entity -> Dto
        public static FixedJobResponseDto toDto(CustomizeJob j){
            return FixedJobResponseDto.builder()
                    .id(j.getId())
                    .name(j.getName())
                    .label(j.getLabel())
                    .startTime(j.getStartTime())
                    .endTime(j.getEndTime())
                    .estimatedTime(j.getEstimatedTime())
                    .isComplete(j.isComplete())
                    .isFixed(j.isFixed())
                    .startDate(DateTimeHandler.dateToStr(j.getStartDate()))
                    .deadline(j.getDeadline())
//                    .completion(j.getCompletion())
                    .build();
        }

    }

    @Getter
    @Builder
    public static class AdjustJobResponseDto{
        private final Long id;
        private final String name;
        private final Integer label;
        private final String estimatedTime;
        private final boolean isComplete;
        private final String startDate;
        private final String deadline;
        private final boolean isFixed;
        private final boolean shouldClear;

        // CustomizeJob Entity -> Dto
        public static AdjustJobResponseDto toDto(CustomizeJob j){
            return AdjustJobResponseDto.builder()
                    .id(j.getId())
                    .name(j.getName())
                    .label(j.getLabel())
                    .estimatedTime(j.getEstimatedTime())
//                    .isPrivate(j.isPrivate())
                    .isComplete(j.isComplete())
                    .isFixed(j.isFixed())
                    .startDate(String.valueOf(j.getStartDate()))
                    .deadline(j.getDeadline())
//                    .completion(j.getCompletion())
                    .build();
        }

    }

    @Getter
    @SuperBuilder
    public static class SeperatedJobResponseDto extends ScheduleDto{
        private final Integer completion;
        private final boolean isFixed;
        private final boolean isComplete;

        public static SeperatedJobResponseDto toSeperatedJob(ScheduleDto scheduleDto){
            return SeperatedJobResponseDto.builder()
                    .name(scheduleDto.getName())
                    .label(scheduleDto.getLabel())
                    .day(scheduleDto.getDay())
                    .startTime(scheduleDto.getStartTime())
                    .endTime(scheduleDto.getEndTime())
                    .estimatedTime(scheduleDto.getEstimatedTime())
                    .deadline(scheduleDto.getDeadline())
                    .completion(0)
                    .isFixed(false)
                    .build();
        }

        public static SeperatedJob toEntity(SeperatedJobResponseDto responseDto){
            return SeperatedJob.builder()
                    .name(responseDto.getName())
                    .label(responseDto.getLabel())
                    .startTime(responseDto.getStartTime())
                    .endTime(responseDto.getEndTime())
                    .estimatedTime(responseDto.getEstimatedTime())
//                    .isComplete()
                    .day(DateTimeHandler.strToDate(responseDto.getDay()))
                    .completion(responseDto.getCompletion())
                    .isFixed(true)
                    //todo 조정된 일정 fixed 값 처리
                    .build();
        }

        public static SeperatedJobResponseDto toDto(SeperatedJob seperatedJob) {
            return SeperatedJobResponseDto.builder()
                    .id(seperatedJob.getId())
                    .name(seperatedJob.getName())
                    .label(seperatedJob.getLabel())
                    .startTime(seperatedJob.getStartTime())
                    .endTime(seperatedJob.getEndTime())
                    .estimatedTime(seperatedJob.getEstimatedTime())
                    .isComplete(seperatedJob.isComplete())
                    .day(DateTimeHandler.dateToStr(seperatedJob.getDay()))
                    .completion(seperatedJob.getCompletion())
                    .isFixed(seperatedJob.isFixed())
                    .build();
        }
    }

    @Data
    @JsonInclude
    @NoArgsConstructor
    @SuperBuilder
    public static class ScheduleDto{
        private Long id;
        private String name;
        private Integer label;
        private String day;
        private String startTime;
        private String endTime;
        private String estimatedTime;
        private String startDate;
        private String deadline;
        private boolean shouldClear;

        public static ScheduleDto toScheduleDto(EveryTimeJob everyTimeJob, LocalDate localDate) {
            return ScheduleDto.builder()
                    .id(everyTimeJob.getId())
                    .name(everyTimeJob.getName())
                    .label(everyTimeJob.getLabel()) //항상 0
                    .day(DateTimeHandler.dateToStr(localDate))
                    .startTime(everyTimeJob.getStartTime())
                    .endTime(everyTimeJob.getEndTime())
                    .estimatedTime(everyTimeJob.getEstimatedTime())
                    .startDate(null)
                    .deadline(null) // or simply omit this line
                    .shouldClear(false)
                    .build();
        }


        //CustomizeJob -> ScheduleDto
        public static ScheduleDto toScheduleDto(CustomizeJob j) {
            ScheduleDto scheduleDto = new ScheduleDto();
            scheduleDto.setId(j.getId());
            scheduleDto.setName(j.getName());
            scheduleDto.setShouldClear(j.isShouldClear()); //뒤에 일정 넣는지 안넣는지

            if (j.getStartTime() == null) {
                // Adjust job
                scheduleDto.setLabel(j.getLabel());
                scheduleDto.setEstimatedTime(j.getEstimatedTime());
                scheduleDto.setStartDate(DateTimeHandler.dateToStr(j.getStartDate()));
                scheduleDto.setDeadline(j.getDeadline());
                scheduleDto.setStartTime(null);
                scheduleDto.setEndTime(null);
                scheduleDto.setDay(null);
            } else {
                // Fixed job
                scheduleDto.setLabel(0);
                scheduleDto.setDay(DateTimeHandler.dateToStr(j.getStartDate()));
                scheduleDto.setStartTime(j.getStartTime());
                scheduleDto.setEndTime(j.getEndTime());
                scheduleDto.setEstimatedTime(j.getEstimatedTime());
                scheduleDto.setStartDate(null);
                scheduleDto.setDeadline(null);
            }
            return scheduleDto;
        }

        //SeperatedJob -> ScheduleDto
        public static ScheduleDto toScheduleDto(SeperatedJob seperatedJob) {
            return ScheduleDto.builder()
                    .id(seperatedJob.getId())
                    .name(seperatedJob.getName())
                    .label(0)
                    .day(DateTimeHandler.dateToStr(seperatedJob.getDay()))
                    .startTime(seperatedJob.getStartTime())
                    .endTime(seperatedJob.getEndTime())
                    .estimatedTime(seperatedJob.getEstimatedTime())
                    .startDate(null)
                    .deadline(null)
                    .shouldClear(false)
                    .build();
        }
    }

    @Data
    public static class AdjustmentDto{
        private List<String> Week_day;
        private String schedule_startTime;
        private List<ScheduleDto> Schedule;
        private List<DefaultJobResponseDto> defaultJobs;
    }


    @Data
    @JsonInclude
    @NoArgsConstructor(force = true)
    @SuperBuilder
    public static class DefaultJobResponseDto{
        private final String name;
        private final String startTime;
        private final String endTime;
        private final Integer label;
        private final String estimatedTime;
        private final boolean fixed;

        public static DefaultJobResponseDto toResponseDto(Job j){
            return DefaultJobResponseDto.builder()
                    .name(j.getName())
                    .startTime(j.getStartTime())
                    .endTime(j.getEndTime())
                    .label(j.getLabel())
                    .estimatedTime(j.getEstimatedTime())
                    .fixed(j.isFixed())
                    .build();
        }
    }





}
