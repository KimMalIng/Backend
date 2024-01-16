package com.capstone.AreyouP.calendar;

import com.capstone.AreyouP.timetable.TimeTable;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@Entity
@ToString(exclude="timeTables")
public class Calendar {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique=true)
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
