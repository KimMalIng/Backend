package com.capstone.AreyouP.DTO.Schedule;

import com.capstone.AreyouP.Domain.Job;
import lombok.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class JobDto {
    private Long user_id;
    private Long job_id;
    private String day;
    private String startTime;
    private String endTime;
    private Integer label;
    private String name;
    private String deadline; //String으로 받은 후 Date 형식으로 바꿔서 Job Entity에 넣어야 함
    private String estimated_time;



    private boolean isPrivate;
    private boolean isComplete;

    @Builder
    public JobDto(Long user_id, Long job_id, String day, String startTime, String endTime, Integer label,
                  String name, String deadline, String estimated_time,
                  boolean isPrivate, boolean isComplete){
        this.user_id = user_id;
        this.job_id = job_id;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.label = label;
        this.name = name;
        this.deadline = deadline;
        this.estimated_time = estimated_time;
        this.isPrivate = isPrivate;
        this.isComplete = isComplete;
    }

    public Job toJobEntity(JobDto job) throws ParseException {
        return Job.builder()
                .startTime(job.getStartTime())
                .endTime(job.getEndTime())
                .label(job.getLabel())
                .name(job.getName())
                .deadline(String.valueOf(job.getDeadline()))
                .estimated_time(job.getEstimated_time())
                .isPrivate(job.isPrivate())
                .isComplete(job.isComplete())
                .build();
    }



}
