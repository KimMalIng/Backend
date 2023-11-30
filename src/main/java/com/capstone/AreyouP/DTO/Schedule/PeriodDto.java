package com.capstone.AreyouP.DTO.Schedule;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PeriodDto {
    private Long user_id;
    private String startDate;
    private String endDate;
}
