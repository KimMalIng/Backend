package com.capstone.AreyouP.timetable.dto;

import com.capstone.AreyouP.job.dto.JobDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TimeLineDto {
    private String day;
    private List<JobDto> subject;
}
