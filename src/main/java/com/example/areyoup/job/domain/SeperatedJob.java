package com.example.areyoup.job.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("S")
@NoArgsConstructor
@Getter
public class SeperatedJob extends Job{

    //CustomizeJob에서 조정된 일정
    private LocalDate day; //일정이 배치된 날짜
    private Integer completion = 0; //완료도
    private boolean isFixed;

}
