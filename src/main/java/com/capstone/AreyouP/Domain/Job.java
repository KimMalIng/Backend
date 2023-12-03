package com.capstone.AreyouP.Domain;

import com.capstone.AreyouP.DTO.Schedule.JobDto;
import jakarta.persistence.*;
import lombok.*;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@Entity
@ToString(exclude = {"timeTables", "seperatedJobList"})
public class Job {
    @Id
    @GeneratedValue
    private Long id;

    private String startTime;
    private String endTime;
    private Integer label;
    private String name;
    private String deadline;
    private String estimated_time;
    private boolean isPrivate=false;
    private boolean isComplete=false;

    @OneToMany(mappedBy = "job")
    private List<TimeTable> timeTables= new ArrayList<>();

    @Builder
    public Job(Integer label, String startTime,
               String endTime, String name,
               String deadline, String estimated_time,
               boolean isPrivate, boolean isComplete){
        this.label = label;
        this.startTime = startTime;
        this.endTime = endTime;
        this.deadline = deadline;
        this.name = name;
        this.estimated_time = estimated_time;
        this.isPrivate = isPrivate;
        this.isComplete = isComplete;

    }

    public JobDto toJobDto(Job job) throws ParseException {
        return JobDto.builder()
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
