package com.example.areyoup.job.controller;

import com.example.areyoup.job.dto.JobRequestDto.FixedJobRequestDto;
import com.example.areyoup.job.dto.JobRequestDto.AdjustJobRequestDto;
import com.example.areyoup.job.dto.JobResponseDto;
import com.example.areyoup.job.service.JobService;
import com.example.areyoup.job.dto.everytime.EverytimeRequestDto.EverytimeDto;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController //ResponseBody + Controller 로 Data(Json) 반환하는 어노테이션
@RequiredArgsConstructor
@RequestMapping("/job")
public class JobController {

    private final JobService jobService;

    /*
    유저에 대한 everytime 시간표 저장
     */
    @PostMapping("/{member_id}/everytime")
    public ResponseEntity<String> saveEveryTime(@RequestBody List<EverytimeDto> everytimeDtos,
                                                @PathVariable("member_id") Long member_id) throws ParseException{
        return ResponseEntity.ok()
                .body(jobService.saveEveryTime(everytimeDtos, member_id));
    }

    /*
    고정 일정 저장
     */
    @PostMapping("/fix/save")
    public ResponseEntity<JobResponseDto.CustomizeJobResponseDto> saveFixedJob(@RequestBody FixedJobRequestDto fixedJob){
        return ResponseEntity.ok()
                .body(jobService.saveFixedJob(fixedJob));
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