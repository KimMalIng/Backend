package com.example.areyoup.global.function;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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
    public static String cal_estimatedTime(Integer completion, String seperated_et, String custom_et) {
        float percent = (float) completion/100;
        LocalTime seperated = DateTimeHandler.strToTime(seperated_et);
        LocalTime custom = DateTimeHandler.strToTime(custom_et);

        long seperated_minute = ((long) (( seperated.toSecondOfDay() / 60) * percent));
        //완료한 만큼의 시간(분) 계산하기
        LocalTime result = custom.minusMinutes(seperated_minute);
        //진행한 만큼 뺀다.

        return String.valueOf(result);
    }


    /*
    시작 ~ 끝 시간이 주어지면 소요시간 구하기
     */
    public static String cal_estimatedTime(String startTime, String endTime) {
        LocalTime start = DateTimeHandler.strToTime(startTime);
        LocalTime end = DateTimeHandler.strToTime(endTime);

        Duration duration = Duration.between(start, end);

        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;

        return String.format("%02d:%02d", hours, minutes);
    }

}
