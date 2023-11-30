package com.capstone.AreyouP.DTO.EveryTime;

import com.capstone.AreyouP.DTO.Schedule.JobDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TimeLine {
    private String day;
    private List<JobDto> subject;
}
