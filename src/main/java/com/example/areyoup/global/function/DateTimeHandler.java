package com.example.areyoup.global.function;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class DateTimeHandler {

    public static LocalTime strToTime(String time){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        return LocalTime.parse(time, dtf);
    }

    /*
    String -> Date
     */
    public static LocalDate strToDate(String date){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return LocalDate.parse(date, dtf);
    }

    public static String dateToStr(LocalDate date){
        return String.valueOf(date).replace("-", ".");
    }

    public static LocalDateTime strToDateTime(String date){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime dt = LocalTime.parse(date, dtf);
        LocalDate ld = LocalDate.of(1,1,1);
        return dt.atDate(ld);
    }
}
