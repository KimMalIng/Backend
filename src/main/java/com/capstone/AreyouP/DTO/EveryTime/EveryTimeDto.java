package com.capstone.AreyouP.DTO.EveryTime;


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
    private List<TimeLine> timeline;
}
