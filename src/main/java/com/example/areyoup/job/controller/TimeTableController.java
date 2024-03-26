package com.example.areyoup.job.controller;

import com.example.areyoup.job.dto.JobRequestDto;
import com.example.areyoup.job.service.TimeTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
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
   - CustomizeJob는 분리된 일정이므로 날짜로 반환
    */
    @PostMapping("/period")
    public ResponseEntity<HashMap<String, List>> getTimeTable(@RequestBody JobRequestDto.PeriodRequestDto periodDto) throws ParseException {
        return ResponseEntity.ok()
                .body(timeTableService.getTable(periodDto));
    }
}
