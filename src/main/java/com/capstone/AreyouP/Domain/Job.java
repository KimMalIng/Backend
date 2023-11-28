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
public class Job {
    @Id
    @GeneratedValue
    private Long id;

    private Integer label;
    private String startTime;
    private String endTime;
    private String jobName;
    private Date deadLine;
    private Integer estimated_Time;

    @OneToMany(mappedBy = "job")
    private List<TimeTable> timeTables= new ArrayList<>();

    @Builder
    public Job(Integer label, String startTime, String endTime, String jobName, Date deadLine, Integer estimated_Time){
        this.label = label;
        this.startTime = startTime;
        this.endTime = endTime;
        this.deadLine = deadLine;
        this.jobName = jobName;
        this.estimated_Time = estimated_Time;

    }

}
