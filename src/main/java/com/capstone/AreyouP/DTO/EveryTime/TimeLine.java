package com.capstone.AreyouP.DTO.EveryTime;

import com.capstone.AreyouP.DTO.ScheduleDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TimeLine {
    private Integer day;
    private List<ScheduleDto> subject;
}
