package com.capstone.AreyouP.Controller;

import com.capstone.AreyouP.DTO.EveryTime.EveryTimeDto;
import com.capstone.AreyouP.DTO.Schedule.ScheduleDto;
import com.capstone.AreyouP.Domain.Job;
import com.capstone.AreyouP.Service.JobService;
import com.capstone.AreyouP.Service.TimeTableService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/job")
public class JobController {

    private final JobService jobService;

    @PostMapping("/everytime") //에브리타임 시간표 저장
    public ResponseEntity<?> saveEveryTime(@RequestBody List<EveryTimeDto> everyTimeDtoList){
        return ResponseEntity.status(HttpStatus.OK).body(jobService.saveEveryTime(everyTimeDtoList));
    }

    @PostMapping("/")
    public ResponseEntity<?> saveJob(@RequestBody ScheduleDto scheduleDto){
        return ResponseEntity.status(HttpStatus.OK).body(jobService.saveJob(scheduleDto));
    }

    @PostMapping("/get") //유저에 대한 일정 모두 반환
    public ResponseEntity<List<Job>> getJob(@RequestParam Long user_id){
        return ResponseEntity.status(HttpStatus.OK).body(jobService.getJob(user_id));
    }

}
