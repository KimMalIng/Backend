package com.capstone.AreyouP.Domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@Entity
public class Calendar {
    @Id
    @GeneratedValue
    private Long id;

    private Date Date;
    private Integer year;
    private Integer month;
    private Integer day;
    private Integer week;
    private String dayOfWeek; //0-6 월-일
    private Boolean Holiday;

    @OneToMany(mappedBy = "calendar")
    private List<TimeTable> timeTables= new ArrayList<>();

    @Builder
    public Calendar(Date date, Integer year, Integer month, Integer day, Integer week, String dayOfWeek, Boolean Holiday){
        this.Date = date;
        this.year = year;
        this.month = month;
        this.day = day;
        this.week = week;
        this.dayOfWeek = dayOfWeek;
        this.Holiday = Holiday;

    }


}
