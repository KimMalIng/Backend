package com.capstone.AreyouP.DTO;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {
    private String Date;
    private String startTime;
    private String endTime;
    private Integer label;
    private String name;
    private Integer Estimated_time;
}
