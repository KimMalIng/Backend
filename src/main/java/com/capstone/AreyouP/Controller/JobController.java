package com.capstone.AreyouP.Controller;

import com.capstone.AreyouP.DTO.EveryTime.EveryTimeDto;
import com.capstone.AreyouP.Service.JobService;
import com.capstone.AreyouP.Service.TimeTableService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
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

}
