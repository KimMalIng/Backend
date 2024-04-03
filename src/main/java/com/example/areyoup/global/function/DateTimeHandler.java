package com.example.areyoup.global.function;

import java.time.LocalDate;
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
}
