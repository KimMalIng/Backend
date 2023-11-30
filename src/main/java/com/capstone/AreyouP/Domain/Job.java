package com.capstone.AreyouP.Domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@Entity
@ToString
public class Job {
    @Id
    @GeneratedValue
    private Long id;

    private String startTime;
    private String endTime;
    private Integer label;
    private String name;
    private Date deadLine;
    private Integer estimated_Time;
    private boolean isPrivate=false;
    private boolean isComplete=false;

    @OneToMany(mappedBy = "job")
    private List<TimeTable> timeTables= new ArrayList<>();

    @OneToMany(mappedBy = "job")
    private List<Seperated_Job> seperatedJobList = new ArrayList<>();

    @Builder
    public Job(Integer label, String startTime,
               String endTime, String name,
               Date deadLine, Integer estimated_Time,
               boolean isPrivate, boolean isComplete){
        this.label = label;
        this.startTime = startTime;
        this.endTime = endTime;
        this.deadLine = deadLine;
        this.name = name;
        this.estimated_Time = estimated_Time;
        this.isPrivate = isPrivate;
        this.isComplete = isComplete;

    }

}
