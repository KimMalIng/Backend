package com.example.areyoup.job.dto;

import com.example.areyoup.job.domain.BasicJob;
import com.example.areyoup.job.domain.CustomizeJob;
import com.example.areyoup.job.service.JobService;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
public class JobResponseDto {

//    private Long user_id;
//    private Long job_id;

    //Job
    private String name;
    private Integer label;
    private String startTime;
    private String endTime;
    private String estimated_time;
//    private boolean isPrivate;
    private boolean isComplete;

    //BasicJob
//    private String dayOfTheWeek;

    //CustomizeJob
//    private String day;
//    private String deadline;
//    private String completion;
//    private boolean isFixed;
//    private boolean shouldClear;


    public JobResponseDto(String name, Integer label,
                          String startTime, String endTime,
                          String estimated_time, boolean isComplete) {
        this.name = name;
        this.label = label;
        this.startTime = startTime;
        this.endTime = endTime;
        this.estimated_time = estimated_time;
//        this.isPrivate = isPrivate;
        this.isComplete = isComplete;
//        this.isFixed = isFixed;
//        this.day = day;
    }



    @Getter
    public static class BasicJobResponseDto extends JobResponseDto{

        private final Integer dayOfTheWeek;


        @Builder
        public BasicJobResponseDto(String name, Integer label, String startTime, String endTime, String estimated_time, String day, boolean isPrivate, boolean isComplete, boolean isFixed, Integer dayOfTheWeek) {
            super(name, label, startTime, endTime, estimated_time, isComplete);
            this.dayOfTheWeek = dayOfTheWeek;
        }

        public static BasicJobResponseDto toDto(BasicJob j){
            return BasicJobResponseDto.builder()
                    .name(j.getName())
                    .label(j.getLabel())
                    .startTime(j.getStartTime())
                    .endTime(j.getEndTime())
                    .estimated_time(j.getEstimated_time())
//                    .isPrivate(j.isPrivate())
                    .isComplete(j.isComplete())
//                    .isFixed(j.isFixed())
                    .dayOfTheWeek(j.getDayOfTheWeek())
                    .build();
        }

    }

    @Getter
    public static class FixedJobResponseDto extends JobResponseDto{
        private final String startDate;
        private final String deadline;
        private final boolean isFixed;
        private final boolean shouldClear;


        @Builder
        public FixedJobResponseDto(String name, Integer label,
                                       String startTime, String endTime,
                                       String estimated_time, String startDate, String deadline, Integer completion,
                                       boolean isComplete, boolean isFixed, boolean shouldClear) {
            super(name, label, startTime, endTime, estimated_time, isComplete);
            this.startDate = startDate;
            this.deadline = deadline;
            this.isFixed = isFixed;
            this.shouldClear = shouldClear;
        }

        // CustomizeJob Entity -> Dto
        public static FixedJobResponseDto toDto(CustomizeJob j){
            return FixedJobResponseDto.builder()
                    .name(j.getName())
                    .label(j.getLabel())
                    .startTime(j.getStartTime())
                    .endTime(j.getEndTime())
                    .estimated_time(j.getEstimated_time())
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
    public static class AdjustJobResponseDto{
        private String name;
        private Integer label;
        private String estimated_time;
        private boolean isComplete;
        private final String startDate;
        private final String deadline;
        private final boolean isFixed;
        private final boolean shouldClear;


        @Builder
        public AdjustJobResponseDto(String name, Integer label,
                                   String estimated_time, String startDate, String deadline, Integer completion,
                                   boolean isComplete, boolean isFixed, boolean shouldClear) {
            this.name = name;
            this.label = label;
            this.estimated_time = estimated_time;
            this.isComplete = isComplete;
            this.startDate = startDate;
            this.deadline = deadline;
            this.isFixed = isFixed;
            this.shouldClear = shouldClear;
        }

        // CustomizeJob Entity -> Dto
        public static AdjustJobResponseDto toDto(CustomizeJob j){
            return AdjustJobResponseDto.builder()
                    .name(j.getName())
                    .label(j.getLabel())
                    .estimated_time(j.getEstimated_time())
//                    .isPrivate(j.isPrivate())
                    .isComplete(j.isComplete())
                    .isFixed(j.isFixed())
                    .startDate(String.valueOf(j.getStartDate()))
                    .deadline(j.getDeadline())
//                    .completion(j.getCompletion())
                    .build();
        }

    }


    @Data
    public static class TimeLineResponseDto{
        private String day;
        private List<JobResponseDto> subject;
    }

    public static class AdjustmentDto{
        private List<String> Week_day;
        private String schedule_startTime;
        private List<JobResponseDto> Schedule;
    }

}
