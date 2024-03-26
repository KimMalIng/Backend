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
    public static class CustomizeJobResponseDto extends JobResponseDto{
        private final String day;
        private final String deadline;
        private final Integer completion;
        private final boolean isFixed;
        private final boolean shouldClear;


        @Builder
        public CustomizeJobResponseDto(String name, Integer label,
                                       String startTime, String endTime,
                                       String estimated_time, String day, String deadline, Integer completion,
                                       boolean isComplete, boolean isFixed, boolean shouldClear) {
            super(name, label, startTime, endTime, estimated_time, isComplete);
            this.day = day;
            this.deadline = deadline;
            this.completion = completion;
            this.isFixed = isFixed;
            this.shouldClear = shouldClear;
        }

        // CustomizeJob Entity -> Dto
        public static CustomizeJobResponseDto toDto(CustomizeJob j){
            return CustomizeJobResponseDto.builder()
                    .name(j.getName())
                    .label(j.getLabel())
                    .startTime(j.getStartTime())
                    .endTime(j.getEndTime())
                    .estimated_time(j.getEstimated_time())
//                    .isPrivate(j.isPrivate())
                    .isComplete(j.isComplete())
                    .isFixed(j.isFixed())
                    .day(String.valueOf(j.getDay()))
                    .deadline(j.getDeadline())
                    .completion(j.getCompletion())
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
