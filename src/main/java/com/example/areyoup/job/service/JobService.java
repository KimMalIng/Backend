package com.example.areyoup.job.service;

import com.example.areyoup.errors.errorcode.JobErrorCode;
import com.example.areyoup.errors.exception.JobException;
import com.example.areyoup.job.domain.CustomizeJob;
import com.example.areyoup.job.domain.Job;
import com.example.areyoup.job.dto.JobRequestDto;
import com.example.areyoup.job.dto.JobResponseDto;
import com.example.areyoup.job.repository.JobRepository;
import com.example.areyoup.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
//final 변수 생성자
@Slf4j
public class JobService {

    private final JobRepository jobRepository;
    private final MemberRepository memberRepository;


    /*
    일정 소요시간 계산하여 "HH:MM" 형식으로 설정
     */
    public static String cal_Time(String s, String e) {

        //HH:mm 형식으로 모두 초기화
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime startTime = LocalTime.parse(s, dtf);
        LocalTime endTime = LocalTime.parse(e, dtf);

        //시작 시간과, 끝나는 시간으로 소요 시간을 계산
        Duration duration = Duration.between(startTime, endTime);
        long estimatedTimeInMinutes = duration.toMinutes();

        int hours = (int) (estimatedTimeInMinutes / 60);
        int minutes = (int) (estimatedTimeInMinutes % 60);
        //시간:분 형식으로 초기화
        return String.format("%02d:%02d", hours, minutes);
    }

    /*
    고정 일정 저장
    - isFixed = true
    - startDate(day) == endDate(deadline)
    - shouldClear 이 뒤에 일정을 놓지 않을 것인가?
     */
    public JobResponseDto.FixedJobResponseDto saveFixedJob(JobRequestDto.FixedJobRequestDto fixedJob) {
        CustomizeJob job = JobRequestDto.FixedJobRequestDto.toEntity(fixedJob);
        jobRepository.save(job);
        return JobResponseDto.FixedJobResponseDto.toDto(job);
    }

    /*
    추후 조정할 일정 저장
    - isFixed = false
    - startTime, endTime = null
    -
     */
    public JobResponseDto.AdjustJobResponseDto savedAdjustJob(JobRequestDto.AdjustJobRequestDto adjustJob){
        CustomizeJob job = JobRequestDto.AdjustJobRequestDto.toEntity(adjustJob);
        jobRepository.save(job);
        return JobResponseDto.AdjustJobResponseDto.toDto(job);
    }

    /*
    일정 고정
     */
    public JobResponseDto fixJob(Long id) {
        Job j = jobRepository.findById(id)
                .orElseThrow(() -> new JobException(JobErrorCode.JOB_NOT_FOUND));
        j.toFixUpdate(j.isFixed());
        jobRepository.save(j);
        return JobResponseDto.toDto(j);
    }





//    public List<AdjustmentDto> getJob(Long memberId) {
//        List<Job> jobs = jobRepository.findJobsByMemberId(memberId);
//        if (jobs.isEmpty()) throw new JobException(JobErrorCode.JOB_NOT_FOUND);
//        else{
//            List<AdjustmentDto> adjustmentDtos = new ArrayList<>();
//            for (Job j : jobs){
//                AdjustmentDto adjustmentDto =
//            }
//        }
//    }

    }