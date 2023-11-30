package com.capstone.AreyouP.Controller;

import com.capstone.AreyouP.DTO.EveryTime.TimeLine;
import com.capstone.AreyouP.DTO.Schedule.AdjustmentDto;
import com.capstone.AreyouP.DTO.Schedule.PeriodDto;
import com.capstone.AreyouP.Service.TimeTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/timetable")
public class TimeTableController {

    private final TimeTableService timeTableService;

    @PostMapping("/period")
    public ResponseEntity<List<TimeLine>> getTable(@RequestBody PeriodDto periodDto){
        return ResponseEntity.status(HttpStatus.OK)
                .body(timeTableService.getTable(periodDto.getStartDate(),
                        periodDto.getEndDate(), periodDto.getUser_id()));
    }

    @GetMapping("/adjustment")
    public ResponseEntity<AdjustmentDto> adjustSchedule(@RequestBody PeriodDto periodDto){
        return ResponseEntity.status(HttpStatus.OK)
                .body(timeTableService.adjustSchedule(periodDto.getStartDate(),
                periodDto.getEndDate(), periodDto.getUser_id()));
    } //python에 넣고 받아오는 것까지 구현해야함




}
