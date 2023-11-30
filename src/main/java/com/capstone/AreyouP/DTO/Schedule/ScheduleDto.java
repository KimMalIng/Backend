package com.capstone.AreyouP.DTO.Schedule;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ScheduleDto {
    private String day;
    private String startTime;
    private String endTime;
    private Integer label;
    private String name;
    private Date deadline;
    private Integer estimated_time;

    @Builder
    public ScheduleDto(String day, String startTime, String endTime, Integer label,
                       String name, Date deadline, Integer Estimated_time){
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.label = label;
        this.name = name;
        this.deadline = deadline;
        this.estimated_time = Estimated_time;
    }
}
