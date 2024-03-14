package com.example.areyoup.job.domain;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
public abstract class Job {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; //일정 이름
    private Integer label; //종류
    private String startTime; //시작 시간
    private String endTime; //끝나는 시간
    private String day; //날짜
    private String estimated_time; //예상 소요 시간
    private boolean isPrivate = false; //일정 private?
    private boolean isFixed = false; //일정 고정?
    private boolean isComplete = false; //일정 완료?

}