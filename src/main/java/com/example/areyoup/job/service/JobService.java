package com.example.areyoup.job.service;

import com.example.areyoup.errors.errorcode.JobErrorCode;
import com.example.areyoup.errors.exception.JobException;
import com.example.areyoup.everytime.domain.EveryTimeJob;
import com.example.areyoup.everytime.dto.EveryTimeResponseDto;
import com.example.areyoup.everytime.repository.EveryTimeJobRepository;
import com.example.areyoup.global.function.CalTime;
import com.example.areyoup.job.domain.CustomizeJob;
import com.example.areyoup.job.domain.Job;
import com.example.areyoup.job.domain.SeperatedJob;
import com.example.areyoup.job.dto.JobRequestDto;
import com.example.areyoup.job.dto.JobResponseDto;
import com.example.areyoup.job.repository.CustomizeJobRepository;
import com.example.areyoup.job.repository.JobRepository;
import com.example.areyoup.job.repository.SeperatedJobRepository;
import com.example.areyoup.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
//final 변수 생성자
@Slf4j
public class JobService {

    private final JobRepository jobRepository;
    private final SeperatedJobRepository seperatedJobRepository;
    private final CustomizeJobRepository customizeJobRepository;
    private final EveryTimeJobRepository everyTimeJobRepository;

    /*
    고정 일정 저장
    - isFixed = true
    - startDate(day) == endDate(deadline)
    - shouldClear 이 뒤에 일정을 놓지 않을 것인가?
     */
    public JobResponseDto.FixedJobResponseDto saveFixedJob(JobRequestDto.FixedJobRequestDto fixedJob) {
        CustomizeJob job = JobRequestDto.FixedJobRequestDto.toEntity(fixedJob);
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
    public JobResponseDto.AdjustJobResponseDto savedAdjustJob(JobRequestDto.AdjustJobRequestDto adjustJob){
        CustomizeJob job = JobRequestDto.AdjustJobRequestDto.toEntity(adjustJob);
        jobRepository.save(job);
        log.info("조정 해야 할 일정(Adjust Job) 저장");
        return JobResponseDto.AdjustJobResponseDto.toDto(job);
    }

    /*
    일정 고정
     */
    public JobResponseDto fixJob(Long id) {
        Job j = jobRepository.findById(id)
                .orElseThrow(() -> new JobException(JobErrorCode.JOB_NOT_FOUND));
        if (j.isFixed()) log.info("일정 {} unfixed", id);
        else log.info("일정 {} fixed", id);
        j.toFixUpdate(j.isFixed());
        return JobResponseDto.toDto(j);
    }

    /*
    SeperatedJob에서 완료도 받고 CustomJob의 소요 시간 줄이기
     */

    public JobResponseDto.AdjustJobResponseDto getCompletion(Long jobId, Integer completion) {
        if (completion != null) { //조정 일정부분
            SeperatedJob seperatedJob = seperatedJobRepository.findById(jobId)
                    .orElseThrow(() -> new JobException(JobErrorCode.JOB_NOT_FOUND));

//            boolean isComplete = completion == 100; //완료도 100인 경우 완료한 것
            //todo 완료랑 성공은 다른거 아닌가?
            seperatedJob.toUpdateCompletion(completion, true); //완료도와 완료 여부 업데이트

            CustomizeJob customizeJob = customizeJobRepository.findByName(seperatedJob.getName()); //원래 일정의 소요시간
            String estimatedTime = CalTime.cal_estimatedTime(seperatedJob.getCompletion(), seperatedJob.getEstimatedTime(), customizeJob.getEstimatedTime());
            customizeJob.toUpdateEstimatedTime(estimatedTime);
            log.info(" {} - 일정 '{}' 완료 후 예상 소요 시간 조정", seperatedJob.getDay(), seperatedJob.getName());
            log.info("일정 '{}'의 남은 예상 시간 : {}", customizeJob.getName(), customizeJob.getEstimatedTime());

            return JobResponseDto.AdjustJobResponseDto.toDto(customizeJob);
        } else { //고정된 일정
            CustomizeJob customizeJob = customizeJobRepository.findById(jobId)
                    .orElseThrow(() -> new JobException(JobErrorCode.JOB_NOT_FOUND));
            customizeJob.toUpdateComplete(customizeJob.isComplete());
            customizeJobRepository.save(customizeJob);
            log.info("{} - 일정 '{}' 완료", customizeJob.getStartDate(), customizeJob.getName());
            return JobResponseDto.AdjustJobResponseDto.toDto(customizeJob);
        }
    }

    /*
    회원의 일정 가져오기
    - 조정된 일정 세세한 일정 x 큰 일정만
     */
    public HashMap<String, List> findAllJob() {
        /// TODO: 2024-03-30 회원정보가져오기
        Long memberId = 1L;
        //날짜들의 요일에 해당되는 에타 시간표(Basic Jobs)를 가져온다.
        List<EveryTimeResponseDto> EveryTimeJobs = getEveryTimeJobs(memberId);

        //새로 배치된 Fixed Job을 가져오는 과정
        HashMap<String, List> Jobs = getCustomizeJobs(memberId);
//        List<JobResponseDto.FixedJobResponseDto> Jobs = getCustomizeJobs(memberId);

        HashMap<String, List> jobs = new HashMap<>();
        jobs.put("EveryTimeJob", EveryTimeJobs);
        jobs.putAll(Jobs);

        log.info("회원의 일정들 반환");
        return jobs;
    }

    /*
    사용자가 등록한 일정 가져오기
     */
    private HashMap<String, List> getCustomizeJobs(Long memberId) {
        List<CustomizeJob> seperatedJobs= customizeJobRepository.findAllByMemberId(memberId);
        HashMap<String, List> jobs = new HashMap<>();
        List<CustomizeJob> fixedJobs = new ArrayList<>();
        List<CustomizeJob> adjustJobs = new ArrayList<>();
        for (CustomizeJob c : seperatedJobs){
            if (c.getStartTime() != null){
                fixedJobs.add(c);
            } else{
                adjustJobs.add(c);
            }
        }

        jobs.put("FixedJob", fixedJobs.stream()
                .map(CustomizeJob::toCustomizeJobDto)
                .toList());
        jobs.put("AdjustJob", adjustJobs.stream()
                .map(CustomizeJob::toCustomizeJobDto)
                .toList());

        return jobs;


    }
    private List<EveryTimeResponseDto> getEveryTimeJobs(Long memberId) {
        List<EveryTimeJob> everyTimeJobs = everyTimeJobRepository.findAllByMemberId(memberId);

        return everyTimeJobs.stream()
                .map(EveryTimeJob::toEveryTimeJobDto)
                .toList();
    }

    }