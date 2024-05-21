package com.example.areyoup.job.domain;

import com.example.areyoup.global.entity.BaseEntity;
import com.example.areyoup.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn
@NoArgsConstructor
@Getter
@SuperBuilder
public abstract class Job extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    protected String name; //일정 이름
    @NonNull
    protected Integer label; //종류
    protected String startTime; //시작 시간
    protected String endTime; //끝나는 시간
    protected String estimatedTime; //예상 소요 시간
    protected boolean isFixed; //일정 고정 여부
    protected boolean isComplete; //일정 완료 여부
    protected Integer completion; //완료도


    @ManyToOne(fetch = FetchType.EAGER)
    protected Member member;

    public void toFixUpdate(boolean isFixed) {
        this.isFixed = !isFixed;
    }

    public void toUpdateComplete(boolean complete) {
        this.isComplete = !complete;
    }
}