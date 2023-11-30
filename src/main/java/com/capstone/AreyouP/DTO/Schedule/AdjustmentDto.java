package com.capstone.AreyouP.DTO.Schedule;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AdjustmentDto {
    private List<String> Week_day;
    private List<JobDto> Schedule;
}
