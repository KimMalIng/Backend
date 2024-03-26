package com.example.areyoup.job.domain;

import com.example.areyoup.job.dto.JobResponseDto;
import com.example.areyoup.job.dto.JobResponseDto.FixedJobResponseDto;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("C")
@NoArgsConstructor
@Getter
public class CustomizeJob extends Job{

    //고정된 일정과 앞으로 조정할 일정 저장


    //고정된 일정
    private boolean isFixed; //일정 고정 여부
    private boolean shouldClear = false; //뒤에 일정을 놓을지의 여부

    //앞으로 조정할 일정의 시작 - 마감
    private LocalDate startDate; //시작일
    private String deadline; //마감일



    @Builder
    public CustomizeJob(@NonNull String name, @NonNull Integer label,
                        String startTime, String endTime, String estimated_time,
                        boolean shouldClear, boolean isFixed, boolean isComplete,
                        LocalDate day, String deadline) {
        this.name = name;
        this.label = label;
        this.startTime = startTime;
        this.endTime = endTime;
        this.estimated_time = estimated_time;
//        this.isPrivate = isPrivate;
        this.isFixed = isFixed;
        this.isComplete = isComplete;
        this.startDate = day;
        this.deadline = deadline;
        this.shouldClear = shouldClear;
    }

    public static JobResponseDto.FixedJobResponseDto toCustomizeJobDto(CustomizeJob customizeJob) {
        return JobResponseDto.FixedJobResponseDto.toDto(customizeJob);
    }
}