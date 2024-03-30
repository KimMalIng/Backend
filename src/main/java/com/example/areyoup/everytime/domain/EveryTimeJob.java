package com.example.areyoup.everytime.domain;

import com.example.areyoup.job.domain.Job;
import com.example.areyoup.everytime.dto.EveryTimeResponseDto;
import com.example.areyoup.member.Member;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity
@DiscriminatorValue("E")
@Getter
@NoArgsConstructor
//기본 시간표 (에브리타임 시간표)
public class EveryTimeJob extends Job {

    private Integer dayOfTheWeek; //요일

    @Builder
    public EveryTimeJob(@NonNull String name, @NonNull Integer label,
                        String startTime, String endTime, String estimatedTime,
                        boolean isComplete, boolean isFixed,
                        Member member, Integer dayOfTheWeek) {
        this.name = name;
        this.label = label;
        this.startTime = startTime;
        this.endTime = endTime;
        this.estimatedTime = estimatedTime;
        this.isComplete = isComplete;
        this.isFixed = isFixed;
        this.member = member;
        this.dayOfTheWeek = dayOfTheWeek;
    }

    public static EveryTimeResponseDto toEveryTimeJobDto(EveryTimeJob j){
        return EveryTimeResponseDto.toDto(j);
    }
}