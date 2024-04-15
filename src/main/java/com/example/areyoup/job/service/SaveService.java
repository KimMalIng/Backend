package com.example.areyoup.job.service;

import com.example.areyoup.errors.errorcode.MemberErrorCode;
import com.example.areyoup.errors.exception.MemberException;
import com.example.areyoup.job.domain.CustomizeJob;
import com.example.areyoup.job.domain.Job;
import com.example.areyoup.job.dto.JobRequestDto;
import com.example.areyoup.job.dto.JobResponseDto;
import com.example.areyoup.job.repository.JobRepository;
import com.example.areyoup.member.domain.Member;
import com.example.areyoup.member.repository.MemberRepository;
import com.example.areyoup.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaveService {

    private final JobRepository jobRepository;
    private final MemberService memberService;
    private final HttpServletRequest request;

    /*
    고정 일정 저장
    - isFixed = true
    - startDate(day) == endDate(deadline)
    - shouldClear 이 뒤에 일정을 놓지 않을 것인가?
     */
    public JobResponseDto.FixedJobResponseDto saveFixedJob(JobRequestDto.FixedJobRequestDto fixedJob) {
        Member m = memberService.findMember(request);
        CustomizeJob job = JobRequestDto.FixedJobRequestDto.toEntity(fixedJob, m);
        jobRepository.save(job);
        log.info("고정 일정(Fixed Job) 저장");
        return JobResponseDto.FixedJobResponseDto.toDto(job);
    }

    /*
    추후 조정할 일정 저장
    - isFixed = false
    - startTime, endTime = null
    -
     */
    public JobResponseDto.AdjustJobResponseDto saveAdjustJob(JobRequestDto.AdjustJobRequestDto adjustJob){
        Member m = memberService.findMember(request);
        CustomizeJob job = JobRequestDto.AdjustJobRequestDto.toEntity(adjustJob,m);
        jobRepository.save(job);
        log.info("조정 해야 할 일정(Adjust Job) 저장");
        return JobResponseDto.AdjustJobResponseDto.toDto(job);
    }

    /*
    유저의 기본 일정 저장
    - 취침, 아침, 점심, 저녁 시간 고정
     */
    public List<JobResponseDto.DefaultJobResponseDto> saveDefaultJob(List<JobRequestDto.DefaultJobRequestDto> defaultJobs) {
        Member m = memberService.findMember(request);
        List<Job> jobs = defaultJobs.stream()
                .map(jobRequestDto -> jobRequestDto.toJobEntity(jobRequestDto, m))
                .toList();
        //받은 RequestDto -> Entity로 변환

        jobRepository.saveAll(jobs); //Entity 모두 저장
        log.info("기본 일정(Default Job) 저장");

        return jobs.stream()
                .map(JobResponseDto.DefaultJobResponseDto::toResponseDto)
                .toList(); //Entity -> ResponseDto로 변환

    }
}
