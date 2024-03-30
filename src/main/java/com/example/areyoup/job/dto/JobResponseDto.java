package com.example.areyoup.job.dto;

import com.example.areyoup.everytime.domain.EveryTimeJob;
import com.example.areyoup.job.domain.CustomizeJob;
import com.example.areyoup.job.domain.Job;
import com.example.areyoup.job.domain.SeperatedJob;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
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
    public JobResponseDto(Long id, String name, Integer label,
                          String startTime, String endTime,
                          String estimatedTime, boolean isComplete, boolean isFixed) {
        this.id = id;
        this.name = name;
        this.label = label;
        this.startTime = startTime;
        this.endTime = endTime;
        this.estimatedTime = estimatedTime;
//        this.isPrivate = isPrivate;
        this.isComplete = isComplete;
        this.isFixed = isFixed;
//        this.day = day;
    }

    public static JobResponseDto toDto(Job j) {
        return new JobResponseDto(
        j.getId(), j.getName(), j.getLabel(), j.getStartTime(), j.getEndTime(), j.getEndTime(), j.isComplete(), j.isFixed());
    }


    @Getter
    public static class FixedJobResponseDto extends JobResponseDto{
        private final String startDate;
        private final String deadline;
        private final boolean isFixed;
        private final boolean shouldClear;


        @Builder
        public FixedJobResponseDto(Long id, String name, Integer label,
                                       String startTime, String endTime,
                                       String estimatedTime, String startDate, String deadline, Integer completion,
                                       boolean isComplete, boolean isFixed, boolean shouldClear) {
            super(id, name, label, startTime, endTime, estimatedTime, isComplete, isFixed);
            this.startDate = startDate;
            this.deadline = deadline;
            this.isFixed = isFixed;
            this.shouldClear = shouldClear;
        }

        // CustomizeJob Entity -> Dto
        public static FixedJobResponseDto toDto(CustomizeJob j){
            return FixedJobResponseDto.builder()
                    .id(j.getId())
                    .name(j.getName())
                    .label(j.getLabel())
                    .startTime(j.getStartTime())
                    .endTime(j.getEndTime())
                    .estimatedTime(j.getEstimatedTime())
//                    .isPrivate(j.isPrivate())
                    .isComplete(j.isComplete())
                    .isFixed(j.isFixed())
                    .startDate(String.valueOf(j.getStartDate()).replace("-","."))
                    .deadline(j.getDeadline())
//                    .completion(j.getCompletion())
                    .build();
        }

    }

    @Getter
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


        @Builder
        public AdjustJobResponseDto(Long id, String name, Integer label,
                                   String estimatedTime, String startDate, String deadline, Integer completion,
                                   boolean isComplete, boolean isFixed, boolean shouldClear) {
            this.id= id;
            this.name = name;
            this.label = label;
            this.estimatedTime = estimatedTime;
            this.isComplete = isComplete;
            this.startDate = startDate;
            this.deadline = deadline;
            this.isFixed = isFixed;
            this.shouldClear = shouldClear;
        }

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
    public static class SeperatedJobResponseDto extends ScheduleDto{
        private final Integer completion;
        private final boolean isFixed;
        private final boolean isComplete;

        @Builder
        public SeperatedJobResponseDto(Long id, String name, Integer label, String day, String startTime, String endTime, String estimatedTime, String deadline
                                        ,Integer completion, boolean isFixed, boolean isComplete) {
            super(id, name, label, day, startTime, endTime, estimatedTime, deadline);
            this.completion = completion;
            this.isFixed = isFixed;
            this.isComplete = isComplete;
        }

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
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            LocalDate start = LocalDate.parse(responseDto.getDay(), dtf);

            return SeperatedJob.builder()
                    .name(responseDto.getName())
                    .label(responseDto.getLabel())
                    .startTime(responseDto.getStartTime())
                    .endTime(responseDto.getEndTime())
                    .estimatedTime(responseDto.getEstimatedTime())
//                    .isComplete()
                    .day(start)
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
                    .day(String.valueOf(seperatedJob.getDay()).replace("-","."))
                    .completion(seperatedJob.getCompletion())
                    .isFixed(seperatedJob.isFixed())
                    .build();
        }
    }

    @Data
    @JsonInclude
    @NoArgsConstructor
    public static class ScheduleDto{
        private Long id;
        private String name;
        private Integer label;
        private String day;
        private String startTime;
        private String endTime;
        private String estimatedTime;
        private String deadline;


        public ScheduleDto(Long id, String name, Integer label, String day, String startTime, String endTime, String estimatedTime, String deadline) {
            this.id= id;
            this.name = name;
            this.label = label;
            this.day = day;
            this.startTime = startTime;
            this.endTime = endTime;
            this.estimatedTime = estimatedTime;
            this.deadline = deadline;
        }


        public static ScheduleDto toScheduleDto(EveryTimeJob everyTimeJob, LocalDate localDate) {
            ScheduleDto scheduleDto = new ScheduleDto();
            scheduleDto.setId(everyTimeJob.getId());
            scheduleDto.setName(everyTimeJob.getName());
            scheduleDto.setLabel(everyTimeJob.getLabel());
            scheduleDto.setDay(String.valueOf(localDate).replace("-", "."));
            scheduleDto.setStartTime(everyTimeJob.getStartTime());
            scheduleDto.setEndTime(everyTimeJob.getEndTime());
            scheduleDto.setEstimatedTime(everyTimeJob.getEstimatedTime());
            // Since this is for EveryTimeJob, there is no deadline
            scheduleDto.setDeadline(null); // Or simply omit this line
            return scheduleDto;
        }


        public static ScheduleDto toScheduleDto(CustomizeJob j) {
            ScheduleDto scheduleDto = new ScheduleDto();
            scheduleDto.setId(j.getId());
            scheduleDto.setName(j.getName());
            if (j.getStartTime() == null) {
                // Adjust job
                scheduleDto.setLabel(j.getLabel());
                scheduleDto.setEstimatedTime(j.getEstimatedTime());
                scheduleDto.setDeadline(j.getDeadline());
                scheduleDto.setStartTime(null);
                scheduleDto.setEndTime(null);
                scheduleDto.setDay(null);
            } else {
                // Fixed job
                scheduleDto.setLabel(0);
                scheduleDto.setDay(String.valueOf(j.getStartDate()).replace("-", "."));
                scheduleDto.setStartTime(j.getStartTime());
                scheduleDto.setEndTime(j.getEndTime());
                scheduleDto.setEstimatedTime(j.getEstimatedTime());
                scheduleDto.setDeadline(null);
            }
            return scheduleDto;
        }

        public static ScheduleDto toScheduleDto(SeperatedJob seperatedJob) {
            ScheduleDto scheduleDto = new ScheduleDto();
            scheduleDto.setId(seperatedJob.getId());
            scheduleDto.setName(seperatedJob.getName());
            scheduleDto.setLabel(seperatedJob.getLabel());
            scheduleDto.setDay(String.valueOf(seperatedJob.getDay()).replace("-","."));
            scheduleDto.setStartTime(seperatedJob.getStartTime());
            scheduleDto.setEndTime(seperatedJob.getEndTime());
            scheduleDto.setEstimatedTime(seperatedJob.getEstimatedTime());
            // Since this is for EveryTimeJob, there is no deadline
            scheduleDto.setDeadline(null); // Or simply omit this line
            return scheduleDto;
        }
    }

    @Data
    public static class AdjustmentDto{
        private List<String> Week_day;
        private String schedule_startTime;
        private List<ScheduleDto> Schedule;
    }





}
