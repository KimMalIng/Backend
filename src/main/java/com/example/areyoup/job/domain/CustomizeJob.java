package com.example.areyoup.job.domain;

import com.example.areyoup.global.function.DateTimeHandler;
import com.example.areyoup.job.dto.JobRequestDto;
import com.example.areyoup.job.dto.JobResponseDto;
import com.example.areyoup.job.dto.JobResponseDto.FixedJobResponseDto;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("C")
@NoArgsConstructor
@Getter
@SuperBuilder
public class CustomizeJob extends Job{

    //고정된 일정과 앞으로 조정할 일정 저장


    //고정된 일정
    private boolean shouldClear = false; //뒤에 일정을 놓을지의 여부

    //앞으로 조정할 일정의 시작 - 마감
    private LocalDate startDate; //시작일
    private String deadline; //마감일


    public void toUpdateEstimatedTime(String estimatedTime){
        this.estimatedTime = estimatedTime;
    }

    public void toUpdateComplete(boolean isComplete){
        this.isComplete = !isComplete;
    }

    public static JobResponseDto.FixedJobResponseDto toCustomizeJobDto(CustomizeJob customizeJob) {
        return JobResponseDto.FixedJobResponseDto.toDto(customizeJob);
    }

    public void toUpdateAll(JobRequestDto.UpdateJobRequestDto updateJob) {
        this.name = updateJob.getName();
        this.label = updateJob.getLabel();
        this.startTime = updateJob.getStartTime();
        this.endTime = updateJob.getEndTime();
        this.estimatedTime = updateJob.getEstimatedTime();
        this.isFixed = updateJob.isFixed();
        this.isComplete = updateJob.isComplete();
        this.startDate = DateTimeHandler.strToDate(updateJob.getStartDate());
        this.deadline = updateJob.getDeadline();
    }
}