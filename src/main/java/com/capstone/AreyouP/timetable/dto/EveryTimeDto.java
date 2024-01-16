package com.capstone.AreyouP.timetable.dto;


import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EveryTimeDto {
    private Integer year;
    private String semester;
    private List<TimeLineDto> timeline;
}
