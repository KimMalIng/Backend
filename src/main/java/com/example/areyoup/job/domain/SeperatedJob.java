package com.example.areyoup.job.domain;

import com.example.areyoup.job.dto.JobResponseDto;
import com.example.areyoup.member.Member;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("S")
@NoArgsConstructor
@Getter
public class SeperatedJob extends Job{

    //CustomizeJob에서 조정된 일정
    private LocalDate day; //일정이 배치된 날짜
    private Integer completion = 0; //완료도

    @Builder
    public SeperatedJob(@NonNull String name, @NonNull Integer label,
                    String startTime, String endTime, String estimated_time,
                    boolean isComplete
                    ,LocalDate day, Integer completion, boolean isFixed) {
        this.name = name;
        this.label = label;
        this.startTime = startTime;
        this.endTime = endTime;
        this.estimated_time = estimated_time;
//        this.isPrivate = isPrivate;
        this.isComplete = isComplete;
//        this.member = member;
        this.day = day;
        this.completion = completion;
        this.isFixed = isFixed;
    }


    public static JobResponseDto.SeperatedJobResponseDto toSeperatedJobDto(SeperatedJob seperatedJob) {
        return JobResponseDto.SeperatedJobResponseDto.toDto(seperatedJob);
    }
}
