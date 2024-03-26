package com.example.areyoup.job.domain;

import com.example.areyoup.job.dto.JobResponseDto.BasicJobResponseDto;
import com.example.areyoup.member.Member;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@DiscriminatorValue("B")
@Getter
@NoArgsConstructor
//기본 시간표 (에브리타임 시간표)
public class BasicJob extends Job{

    private Integer dayOfTheWeek; //요일

    @Builder
    public BasicJob(@NonNull String name, @NonNull Integer label,
                    String startTime, String endTime, String estimated_time,
                    boolean isComplete,
                    Member member, Integer dayOfTheWeek) {
        this.name = name;
        this.label = label;
        this.startTime = startTime;
        this.endTime = endTime;
        this.estimated_time = estimated_time;
//        this.isPrivate = isPrivate;
//        this.isFixed = isFixed;
        this.isComplete = isComplete;
        this.member = member;
        this.dayOfTheWeek = dayOfTheWeek;
    }

    public static BasicJobResponseDto toBasicJobDto(BasicJob j){
        return BasicJobResponseDto.toDto(j);
    }
}