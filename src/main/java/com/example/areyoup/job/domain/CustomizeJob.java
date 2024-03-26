package com.example.areyoup.job.domain;

import com.example.areyoup.job.dto.JobResponseDto.CustomizeJobResponseDto;
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

    private LocalDate day; //일정 수행 날짜
    private String deadline; //마감일
    private Integer completion = 0; //완료도
    private boolean isFixed = false; //일정 고정 여부
    private boolean shouldClear = false; //뒤에 일정을 놓을지의 여부


    @Builder
    public CustomizeJob(@NonNull String name, @NonNull Integer label,
                        String startTime, String endTime, String estimated_time,
                        boolean shouldClear, boolean isFixed, boolean isComplete,
                        LocalDate day, String deadline, Integer completion) {
        this.name = name;
        this.label = label;
        this.startTime = startTime;
        this.endTime = endTime;
        this.estimated_time = estimated_time;
//        this.isPrivate = isPrivate;
        this.isFixed = isFixed;
        this.isComplete = isComplete;
        this.day = day;
        this.deadline = deadline;
        this.completion = completion;
        this.shouldClear = shouldClear;
    }

    public static CustomizeJobResponseDto toCustomizeJobDto(CustomizeJob customizeJob) {
        return CustomizeJobResponseDto.toDto(customizeJob);
    }
}