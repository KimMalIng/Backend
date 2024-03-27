package com.example.areyoup.everytime.dto;

import lombok.Data;

import java.util.List;

@Data
public class EverytimeRequestDto {

    @Data
    //에브리타임을 처음 가져와서 분리
    public static class EverytimeDto {
        private String year; //년도
        private String semester; //학기
        private List<TimeLineDto> timeline; //시간표
    }

    @Data
    public static class TimeLineDto {
        private String day; //요일
        private List<SubjectDto> subject; //수업 시간표
    }

    @Data
    public static class SubjectDto {
        private String endTime;
        private String startTime;
        private String name;
    }


}
