package com.capstone.AreyouP.Controller;

import com.capstone.AreyouP.DTO.EveryTime.EveryTimeDto;
import com.capstone.AreyouP.Domain.Job;
import com.capstone.AreyouP.Domain.TimeTable;
import com.capstone.AreyouP.Service.TimeTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/timetable")
public class TimeTableController {

    private final TimeTableService timeTableService;

    @GetMapping("/{startDate}&{endDate}")
    public ResponseEntity<Map<String,List<Job>>> getTable(@PathVariable String startDate, @PathVariable String endDate){
        System.out.println(startDate + endDate);
        return ResponseEntity.status(HttpStatus.OK).body(timeTableService.getTable(startDate, endDate));
    }




}
