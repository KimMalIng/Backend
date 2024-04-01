package com.example.areyoup.job.controller;

import com.example.areyoup.job.dto.JobRequestDto;
import com.example.areyoup.job.dto.JobRequestDto.FixedJobRequestDto;
import com.example.areyoup.job.dto.JobRequestDto.AdjustJobRequestDto;
import com.example.areyoup.job.dto.JobResponseDto;
import com.example.areyoup.job.service.JobService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

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
                .body(jobService.saveAdjustJob(adjustJob));
    }

    /*
    일정 고정
    //todo 넘어오는 일정 모두 수정하고 고정하기
     */
    @PutMapping("/update/fix/{job_id}")
    @Transactional
    public ResponseEntity<JobResponseDto> fixJob(@PathVariable("job_id") Long id){
        return ResponseEntity.ok()
                .body(jobService.fixJob(id));
    }

    /*
    완료도 입력받으면 그만큼 소요시간 줄여주기
    url : /job/complete/12?completion=50
    completion = 0 ~ 100


    일정 완료, 미완료
    url : /job/complete/12
    completion = 100
     */
    @GetMapping("/complete/{job_id}")
    @Transactional
    public ResponseEntity<JobResponseDto.AdjustJobResponseDto> getCompletion(@PathVariable("job_id") Long job_id,
                                                             @RequestParam(value = "completion") Integer completion){
        return ResponseEntity.ok()
                .body(jobService.getCompletion(job_id, completion));
    }



    /*
    유저에 대한 일정 모두 반환
    -조정된 일정이 아닌 큰 일정만 반환
     */
    @GetMapping("/findAll")
    public ResponseEntity<HashMap<String, List>> getJob(){
        return ResponseEntity.ok()
                .body(jobService.findAllJob());
    }

    /*
    일정 수정
    - 수정할 모든 일정들을 배열에 넣어서 보내주기
     */
    @PutMapping("/modify")
    @Transactional
    public ResponseEntity<?> modifyJob(@RequestBody List<JobRequestDto.UpdateJobRequestDto> updateJobs){
        return ResponseEntity.ok()
                .body(jobService.updateJobs(updateJobs));
    }

    /*
    유저마다 취침시간, 아침, 점심, 저녁 입력
     */
    @PostMapping("/default/save")
    public ResponseEntity<List<JobResponseDto.DefaultJobResponseDto>> saveDefaultJob(@RequestBody List<JobRequestDto.DefaultJobRequestDto> defaultJobs){
        return ResponseEntity.ok()
                .body(jobService.saveDefaultJob(defaultJobs));
    }

}