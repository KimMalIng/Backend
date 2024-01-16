package com.capstone.AreyouP.job.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdjustmentDto {
    private List<String> Week_day;
    private String schedule_startTime;
    private List<JobDto> Schedule;
}
