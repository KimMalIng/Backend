package com.example.areyoup.global.function;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

//시간 계산 클래스
public class CalTime {
    /*
    일정 소요시간 계산하여 "HH:MM" 형식으로 설정
     */
    public static String cal_Time(String s, String e) {

        //HH:mm 형식으로 모두 초기화
        LocalTime startTime = DateTimeHandler.strToTime(s);
        LocalTime endTime = DateTimeHandler.strToTime(e);

        //시작 시간과, 끝나는 시간으로 소요 시간을 계산
        Duration duration = Duration.between(startTime, endTime);
        long estimatedTimeInMinutes = duration.toMinutes();

        int hours = (int) (estimatedTimeInMinutes / 60);
        int minutes = (int) (estimatedTimeInMinutes % 60);
        //시간:분 형식으로 초기화
        return String.format("%02d:%02d", hours, minutes);
    }

    /*
    완료도 받아서 예상 소요 시간 줄이기
     */
    public static String cal_estimatedTime(Integer completion, String custom_et) {
        float percent = (float) completion/100;
        LocalTime custom = DateTimeHandler.strToTime(custom_et);

        long complete_minute = ((long) (( custom.toSecondOfDay() / 60) * percent));
        //완료한 만큼의 시간(분) 계산하기
        LocalTime result = custom.minusMinutes(complete_minute);
        //진행한 만큼 뺀다.

        return String.valueOf(result);
    }


    /*
    시작 ~ 끝 시간이 주어지면 소요시간 구하기
     */
    public static String cal_estimatedTime(String startTime, String endTime) {
        LocalDateTime start = DateTimeHandler.strToDateTime(startTime);
        LocalDateTime end = DateTimeHandler.strToDateTime(endTime);

        if (end.isBefore(start)) {
            end = end.plusDays(1);
        } //23:00 - 08:00 일 경우를 대비

        Duration duration = Duration.between(start, end);

        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;

        return String.format("%02d:%02d", hours, minutes);
    }

    public static String reduce_estimatedTime(String seperate_et, String custom_et){
        LocalTime seperated = DateTimeHandler.strToTime(seperate_et);
        LocalTime customize = DateTimeHandler.strToTime(custom_et);

        Duration duration = Duration.between(seperated, customize);

        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;

        return String.format("%02d:%02d", hours, minutes);
    }

}
