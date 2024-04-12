package com.example.areyoup.everytime.domain;

import com.example.areyoup.job.domain.Job;
import com.example.areyoup.everytime.dto.EveryTimeResponseDto;
import com.example.areyoup.job.dto.JobRequestDto;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("E")
@Getter
@NoArgsConstructor
@SuperBuilder
//기본 시간표 (에브리타임 시간표)
public class EveryTimeJob extends Job {

    private Integer dayOfTheWeek; //요일

    public static EveryTimeResponseDto toEveryTimeJobDto(EveryTimeJob j){
        return EveryTimeResponseDto.toDto(j);
    }

    public void toUpdateAll(JobRequestDto.UpdateJobRequestDto j){
        this.name = j.getName();
        this.label = j.getLabel();
        this.startTime = j.getStartTime();
        this.endTime = j.getEndTime();
        this.estimatedTime = j.getEstimatedTime();
        this.isComplete = j.isComplete();
        this.isFixed = j.isFixed();
        this.dayOfTheWeek = j.getDayOfTheWeek();
    }
}