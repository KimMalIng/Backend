package com.capstone.AreyouP.job.dto;

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
