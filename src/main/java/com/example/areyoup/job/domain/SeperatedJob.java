package com.example.areyoup.job.domain;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("S")
public class SeperatedJob extends Job{

    private String deadline; //마감일
    private Integer completion = 0; //완료도

}