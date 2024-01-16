package com.capstone.AreyouP.job;

import com.capstone.AreyouP.timetable.dto.EveryTimeDto;
import com.capstone.AreyouP.job.dto.JobDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/job")
public class JobController {

    private final JobService jobService;

    @PostMapping("/{user_id}/everytime") //에브리타임 시간표 저장
    public ResponseEntity<?> saveEveryTime(@RequestBody List<EveryTimeDto> everyTimeDtoList,
                                           @PathVariable Long user_id) throws ParseException {
        return ResponseEntity.status(HttpStatus.OK)
                .body(jobService.saveEveryTime(everyTimeDtoList, user_id));
    }

    @PostMapping("/save") //입력된 일정 저장
    public ResponseEntity<?> saveJob(@RequestBody JobDto jobDto){
        return ResponseEntity.status(HttpStatus.OK).body(jobService.saveJob(jobDto, jobDto.getUser_id()));
    } //일정 - 유저 의 정보만 timetable에 저장함. 추후 업데이트 된 일정으로 캘린더와 연결

    @GetMapping("/get/{user_id}") //유저에 대한 일정 모두 반환
    public ResponseEntity<List<JobDto>> getJob(@PathVariable Long user_id) throws ParseException {
        return ResponseEntity.status(HttpStatus.OK).body(jobService.getJob(user_id));
    }

    @PutMapping("/modify") //일정 수정
    public ResponseEntity<Job> modifyJob(@RequestBody JobDto jobDto){
        return ResponseEntity.status(HttpStatus.OK).body(jobService.modifyJob(jobDto));
    }

    @PostMapping("/complete/{job_id}") //일정 완료 or 완료 취소
    public ResponseEntity<Boolean> completeJob(@PathVariable Long job_id){
        return ResponseEntity.status(HttpStatus.OK).body(jobService.completeJob(job_id));
    }

    @DeleteMapping("/{job_id}") //일정 삭제
    public void deleteJob(@PathVariable Long job_id){
        jobService.deleteJob(job_id);
    }

}
