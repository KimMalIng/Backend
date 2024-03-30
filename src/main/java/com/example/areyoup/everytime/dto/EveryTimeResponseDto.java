package com.example.areyoup.everytime.dto;

import com.example.areyoup.everytime.domain.EveryTimeJob;
import com.example.areyoup.job.dto.JobResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
@Getter
public class EveryTimeResponseDto extends JobResponseDto {

    private final Integer dayOfTheWeek;


    @Builder
    public EveryTimeResponseDto(Long id, String name, Integer label, String startTime, String endTime, String estimatedTime, String day, boolean isPrivate, boolean isComplete, boolean isFixed, Integer dayOfTheWeek) {
        super(id, name, label, startTime, endTime, estimatedTime, isComplete, isFixed);
        this.dayOfTheWeek = dayOfTheWeek;
    }


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

    public static EveryTimeResponseDto toAdjustDto(EveryTimeJob j, LocalDate localDate) {
        String day = String.valueOf(localDate).replace("-", ".");
        return EveryTimeResponseDto.builder()
                .name(j.getName())
                .label(j.getLabel())
                .startTime(j.getStartTime())
                .endTime(j.getEndTime())
                .estimatedTime(j.getEstimatedTime())
                .isComplete(j.isComplete())
                .day(day)
                .build();
    }

}

