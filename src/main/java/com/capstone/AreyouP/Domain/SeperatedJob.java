package com.capstone.AreyouP.Domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@ToString(exclude = {"timeTables"})
public class SeperatedJob {
    @Id
    @GeneratedValue
    private Long id;

    private Long job_id;
    private String day;
    private String startTime;
    private String endTime;
    private Integer label;
    private String name;
    private String deadline;
    private Integer completion=0; //완료도
    private boolean isPrivate = false;
    private boolean isComplete=false; //완료의 여부


    @OneToMany(mappedBy = "seperatedJob")
    private List<TimeTable> timeTables= new ArrayList<>();

    @Builder
    public SeperatedJob(Long job_id, String name, String day, Integer label, String startTime, String endTime, Integer completion){
        this.job_id = job_id;
        this.startTime = startTime;
        this.label = label;
        this.endTime = endTime;
        this.name = name;
        this.completion = completion;
        this.day = day;
    }



}
