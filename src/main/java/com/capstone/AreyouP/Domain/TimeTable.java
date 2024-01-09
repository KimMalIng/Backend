package com.capstone.AreyouP.Domain;

import com.capstone.AreyouP.Domain.Member.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@ToString(exclude = {"calendar","member","job","seperatedJob"})
public class TimeTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Calendar calendar;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Job job;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private SeperatedJob seperatedJob;

    @Builder
    public TimeTable(SeperatedJob seperatedJob, Calendar calendar, Member member, Job job){
        this.calendar = calendar;
        this.member = member;
        this.job = job;
        this.seperatedJob = seperatedJob;
    }
}
