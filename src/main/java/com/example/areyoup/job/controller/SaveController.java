package com.example.areyoup.job.controller;

import com.example.areyoup.job.dto.JobRequestDto;
import com.example.areyoup.job.dto.JobResponseDto;
import com.example.areyoup.job.service.JobService;
import com.example.areyoup.job.service.SaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController //ResponseBody + Controller 로 Data(Json) 반환하는 어노테이션
@RequiredArgsConstructor
@RequestMapping("/job/save")
public class SaveController {

    private final SaveService saveService;

    /*
    고정 일정 저장
     */
    @PostMapping("/fix")
    public ResponseEntity<JobResponseDto.FixedJobResponseDto> saveFixedJob(@RequestBody JobRequestDto.FixedJobRequestDto fixedJob){
        return ResponseEntity.ok()
                .body(saveService.saveFixedJob(fixedJob));
    }

    @PostMapping("/adjust")
    public ResponseEntity<JobResponseDto.AdjustJobResponseDto> saveAdjustJob(@RequestBody JobRequestDto.AdjustJobRequestDto adjustJob){
        return ResponseEntity.ok()
                .body(saveService.saveAdjustJob(adjustJob));
    }

    /*
    유저마다 취침시간, 아침, 점심, 저녁 입력
     */
    @PostMapping("/default")
    public ResponseEntity<List<JobResponseDto.DefaultJobResponseDto>> saveDefaultJob(@RequestBody List<JobRequestDto.DefaultJobRequestDto> defaultJobs){
        return ResponseEntity.ok()
                .body(saveService.saveDefaultJob(defaultJobs));
    }

}
