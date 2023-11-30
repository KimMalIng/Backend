package com.capstone.AreyouP.Domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@Entity
public class Seperated_Job {
    @Id
    @GeneratedValue
    Long id;

    String startTime;
    String endTime;
    Integer completion=0; //완료도
    boolean isComplete=false; //완료의 여부

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Job job;

    @Builder
    public Seperated_Job(String startTime, String endTime, Integer completion){
        this.startTime = startTime;
        this.endTime = endTime;
        this.completion = completion;
    }



}
