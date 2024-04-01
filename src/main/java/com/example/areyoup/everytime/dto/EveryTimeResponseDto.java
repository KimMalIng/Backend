package com.example.areyoup.everytime.dto;

import com.example.areyoup.everytime.domain.EveryTimeJob;
import com.example.areyoup.job.dto.JobResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
@Getter
@SuperBuilder
public class EveryTimeResponseDto extends JobResponseDto {

    private final Integer dayOfTheWeek;


    public static EveryTimeResponseDto toDto(EveryTimeJob j){
        return EveryTimeResponseDto.builder()
                .id(j.getId())
                .name(j.getName())
                .label(j.getLabel())
                .startTime(j.getStartTime())
                .endTime(j.getEndTime())
                .estimatedTime(j.getEstimatedTime())
//                    .isPrivate(j.isPrivate())
                .isComplete(j.isComplete())
//                    .isFixed(j.isFixed())
                .dayOfTheWeek(j.getDayOfTheWeek())
                .build();
    }


}

