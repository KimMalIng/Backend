package com.example.areyoup.job.controller;

import com.example.areyoup.job.dto.JobRequestDto.FixedJobRequestDto;
import com.example.areyoup.job.dto.JobRequestDto.AdjustJobRequestDto;
import com.example.areyoup.job.dto.JobResponseDto;
import com.example.areyoup.job.service.JobService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController //ResponseBody + Controller 로 Data(Json) 반환하는 어노테이션
@RequiredArgsConstructor
@RequestMapping("/job")
public class JobController {

    private final JobService jobService;

    /*
    고정 일정 저장
     */
    @PostMapping("/fix/save")
    public ResponseEntity<JobResponseDto.FixedJobResponseDto> saveFixedJob(@RequestBody FixedJobRequestDto fixedJob){
        return ResponseEntity.ok()
                .body(jobService.saveFixedJob(fixedJob));
    }

    @PostMapping("/adjust/save")
    public ResponseEntity<JobResponseDto.AdjustJobResponseDto> saveAdjustJob(@RequestBody AdjustJobRequestDto adjustJob){
        return ResponseEntity.ok()
                .body(jobService.savedAdjustJob(adjustJob));
    }

    /*
    일정 고정
     */
    @PutMapping("/update/fix/{job_id}")
    public ResponseEntity<JobResponseDto> fixJob(@PathVariable("job_id") Long id){
        return ResponseEntity.ok()
                .body(jobService.fixJob(id));
    }

    /*
    완료도 입력받으면 그만큼 소요시간 줄여주기
    completion = 0 ~ 100
    url : /job/complete/12?completion=50
     */
    @GetMapping("/complete/{job_id}")
    public ResponseEntity<JobResponseDto.AdjustJobResponseDto> getCompletion(@PathVariable("job_id") Long job_id,
                                                             @RequestParam(required = true) Integer completion){
        return null;

    }



    /*
    유저에 대한 일정 모두 반환
     */
//    @GetMapping("/get/{member_id}")
//    public ResponseEntity<List<AdjustmentDto>> getJob(@PathVariable("member_id") Long member_id){
//        return ResponseEntity.ok()
//                .body(jobService.getJob(member_id));
//    }

}