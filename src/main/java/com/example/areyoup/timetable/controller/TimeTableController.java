package com.example.areyoup.timetable.controller;

import com.example.areyoup.job.dto.JobRequestDto;
import com.example.areyoup.job.dto.JobResponseDto;
import com.example.areyoup.timetable.service.TimeTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/timetable")
@RequiredArgsConstructor
public class TimeTableController {

    private final TimeTableService timeTableService;

    /*
   startDate ~ endDate 까지의 일정 반환
   - BaseJob 는 고정된 에브리타임 일정이므로 요일로 반환
   - CustomizeJob는 사용자 정의 일정이므로 날짜로 반환
   - SeperatedJob는 분리된 일정이므로 날짜로 반환
   url : /period?startDate=yyyy.MM.dd?endDate=yyyy.MM.dd
    */
    @GetMapping("/period")
    public ResponseEntity<HashMap<String, List>> getTimeTable(@RequestParam(value = "startDate") String start,
                                                              @RequestParam(value = "endDate")String end) {
        return ResponseEntity.ok()
                .body(timeTableService.getTable(start, end));
    }

    /*
    유전 알고리즘을 사용한 스케줄링
     */
    @PostMapping("/adjustment")
    public ResponseEntity<JobResponseDto.AdjustmentDto> adjustSchedule(@RequestBody JobRequestDto.PeriodRequestDto periodDto) throws IOException {
        return ResponseEntity.ok()
                .body(timeTableService.adjustSchedule(periodDto));
    }

    /*
    url : /left?startDate=yyyy.MM.dd?endDate=yyyy.MM.dd
    startDate ~ endDate 까지의 남은 시간 반환
     */
    @GetMapping("/left")
    public ResponseEntity<String> calLeftTime(@RequestParam(value = "startDate") String start,
                                              @RequestParam(value = "endDate")String end){
        return ResponseEntity.ok()
                .body(timeTableService.calLeftTime(start, end));
    }

    @GetMapping("/readjustment")
    public ResponseEntity<JobResponseDto.AdjustmentDto> reAdjustSchedule() throws IOException {
        return ResponseEntity.ok()
                .body(timeTableService.arrangeSeperatedJob());
    }



}
