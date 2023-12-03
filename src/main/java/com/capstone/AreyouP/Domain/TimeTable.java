package com.capstone.AreyouP.Domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@ToString(exclude = {"calendar","user","job","seperatedJob"})
public class TimeTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Calendar calendar;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Job job;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SeperatedJob seperatedJob;

    @Builder
    public TimeTable(SeperatedJob seperatedJob, Calendar calendar, User user, Job job){
        this.calendar = calendar;
        this.user = user;
        this.job = job;
        this.seperatedJob = seperatedJob;
    }
}
