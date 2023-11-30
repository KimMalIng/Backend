package com.capstone.AreyouP.Controller;

import com.capstone.AreyouP.DTO.EveryTime.TimeLine;
import com.capstone.AreyouP.DTO.Schedule.AdjustmentDto;
import com.capstone.AreyouP.DTO.Schedule.ScheduleDto;
import com.capstone.AreyouP.Service.TimeTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/timetable")
public class TimeTableController {

    private final TimeTableService timeTableService;

    @GetMapping("/{startDate}&{endDate}")
    public ResponseEntity<List<TimeLine>> getTable(@PathVariable String startDate, @PathVariable String endDate){
        return ResponseEntity.status(HttpStatus.OK).body(timeTableService.getTable(startDate, endDate));
    }

    @GetMapping("/adjustment/{startDate}&{endDate}")
    public ResponseEntity<AdjustmentDto> adjustSchedule(@PathVariable String startDate, @PathVariable String endDate){
        return ResponseEntity.status(HttpStatus.OK).body(timeTableService.adjustSchedule(startDate, endDate));
    } //python에 넣고 받아오는 것까지 구현해야함




}
