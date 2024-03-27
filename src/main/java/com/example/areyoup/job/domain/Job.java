package com.example.areyoup.job.domain;

import com.example.areyoup.global.entity.BaseEntity;
import com.example.areyoup.member.Member;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
@NoArgsConstructor
@Getter
public class Job extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    protected String name; //일정 이름
    @NonNull
    protected Integer label; //종류
    protected String startTime; //시작 시간
    protected String endTime; //끝나는 시간
    protected String estimated_time; //예상 소요 시간
    protected boolean isFixed; //일정 고정 여부
    protected boolean isComplete = false; //일정 완료 여부

    @ManyToOne(fetch = FetchType.LAZY)
    protected Member member;

    public void toFixUpdate(boolean isFixed) {
        this.isFixed = !isFixed;
    }
}