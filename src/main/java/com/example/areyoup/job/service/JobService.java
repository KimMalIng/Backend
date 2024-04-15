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
import com.example.areyoup.member.domain.Member;
import com.example.areyoup.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
//final 변수 생성자
@Slf4j
public class JobService {

    private final JobRepository jobRepository;
    private final SeperatedJobRepository seperatedJobRepository;
    private final CustomizeJobRepository customizeJobRepository;
    private final EveryTimeJobRepository everyTimeJobRepository;
    private final MemberService memberService;
    private final HttpServletRequest request;
    private final HttpSession httpSession;

    /*
    일정 고정
     */
    public JobResponseDto fixJob(Long id) {
        Job j = jobRepository.findById(id)
                .orElseThrow(() -> new JobException(JobErrorCode.JOB_NOT_FOUND));
        if (j.isFixed()) log.info("Job {} unfixed", id);
        else log.info("Job {} fixed", id);
        j.toFixUpdate(j.isFixed());
        return JobResponseDto.toDto(j);
    }

    /*
    SeperatedJob에서 완료도 받고 CustomJob의 소요 시간 줄이기
     */

    public JobResponseDto.AdjustJobResponseDto getCompletion(Long jobId, Integer completion) {
        Member m = memberService.findMember(request);
        CustomizeJob customizeJob;
        if (completion != null) { //조정 일정부분
            SeperatedJob seperatedJob = seperatedJobRepository.findById(jobId)
                    .orElseThrow(() -> new JobException(JobErrorCode.JOB_NOT_FOUND));

            seperatedJob.toUpdateCompletion(completion, true); //완료도와 완료 여부 업데이트

            customizeJob = customizeJobRepository.findByNameAndMemberId(seperatedJob.getName(), m.getId()); //원래 일정의 소요시간
            String estimatedTime = CalTime.cal_estimatedTime(seperatedJob.getCompletion(), customizeJob.getEstimatedTime());
            customizeJob.toUpdateEstimatedTime(estimatedTime); //예정 소요시간 업데이트

            httpSession.setAttribute("modifyCompletion", customizeJob.getId());
            log.info("{} - '{}' , {}% Complete", seperatedJob.getDay(), seperatedJob.getName(),completion);
            log.info("Left Time of '{}' : {}", customizeJob.getName(), customizeJob.getEstimatedTime());

        } else { //고정된 일정
            customizeJob = customizeJobRepository.findById(jobId)
                    .orElseThrow(() -> new JobException(JobErrorCode.JOB_NOT_FOUND));
            customizeJob.toUpdateComplete(customizeJob.isComplete());
            customizeJobRepository.save(customizeJob);
            log.info("{} - '{}' Complete", customizeJob.getStartDate(), customizeJob.getName());
        }

        return JobResponseDto.AdjustJobResponseDto.toDto(customizeJob);
    }

    /*
    회원의 일정 가져오기
    - 조정된 일정 세세한 일정 x 큰 일정만
     */
    public HashMap<String, List> findAllJob() {
        Long memberId = memberService.findMember(request).getId();
        //날짜들의 요일에 해당되는 에타 시간표(Basic Jobs)를 가져온다.
        List<EveryTimeResponseDto> EveryTimeJobs = getEveryTimeJobs(memberId);

        //새로 배치된 Fixed Job을 가져오는 과정
        HashMap<String, List> Jobs = getCustomizeJobs(memberId);
//        List<JobResponseDto.FixedJobResponseDto> Jobs = getCustomizeJobs(memberId);

        HashMap<String, List> jobs = new HashMap<>();
        jobs.put("EveryTimeJob", EveryTimeJobs);
        jobs.putAll(Jobs);

        log.info("Return Schedule of Member");
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
            if (c.getStartTime() != null){ //고정된 일정만 넣기
                fixedJobs.add(c);
            } else{
                adjustJobs.add(c); //조정할 일정만 넣기
            }
        }

        jobs.put("FixedJob", fixedJobs.stream()
                .map(CustomizeJob::toCustomizeJobDto)
                .toList());
        jobs.put("AdjustJob", adjustJobs.stream()
                .map(CustomizeJob::toCustomizeJobDto)
                .toList());
        //Dto 변환 작업 후 return
        return jobs;


    }

    /*
    에브리타임의 모든 일정들 가져오기
     */
    private List<EveryTimeResponseDto> getEveryTimeJobs(Long memberId) {
        List<EveryTimeJob> everyTimeJobs = everyTimeJobRepository.findAllByMemberId(memberId);

        return everyTimeJobs.stream()
                .map(EveryTimeJob::toEveryTimeJobDto)
                .toList();
    }

    /*
    일정 업데이트
    만약 SeperatedJob가 업데이트 되었다면 CustomizeJob의 예상 소요 시간 변경
     */
    public String updateJobs(List<JobRequestDto.UpdateJobRequestDto> updateJobs) {
        Member m = memberService.findMember(request);
        Set<String> nameOfJobs = new HashSet<>();
        updateJobs.forEach(update -> {
            String name = updateJob(update);
            nameOfJobs.add(name);
        });
        log.info("EveryTimeJob {}, Update", everyTimeCnt);
        log.info("CustomizeJob {}, Update", customizeCnt);
        log.info("SeperatedJob {}, Update", seperatedCnt);
        if (seperatedCnt > 0){
            for (String nameOfJob : nameOfJobs){
                if (nameOfJob.isEmpty()) continue;
                Integer totalMinutes = jobRepository.getTotalEstimatedTimeOfSeperatedJobByName(nameOfJob, m.getId());
                String estimatedTime = String.format("%02d:%02d", (totalMinutes/60),(totalMinutes%60));
                CustomizeJob customizeJob = customizeJobRepository.findByNameAndMemberId(nameOfJob, m.getId());
                customizeJob.toUpdateEstimatedTime(estimatedTime);
                log.info("[SeperatedJob] '{}' Estimated Time Update Success", nameOfJob);
            }
        }
        log.info("All Jobs Update Success");
        return "Jobs Update Success";
    }

    /*
    일정의 종류에 따라 나눠서 업데이트 하는 작업
    - EveryTimeJob, CustomizeJob, SeperatedJob
    - CustomizedJob에서 AdjustJob는 업데이트 No!!
     */
    private int everyTimeCnt = 0;
    private int customizeCnt = 0;
    private int seperatedCnt = 0;
    private String updateJob(JobRequestDto.UpdateJobRequestDto updateJob) {
        String nameofJob = "";
        //에브리타임 일정 업데이트
        if (updateJob.getDayOfTheWeek() != null){
            EveryTimeJob everyTimeJob = everyTimeJobRepository.findById(updateJob.getId())
                    .orElseThrow(() -> new JobException(JobErrorCode.JOB_NOT_FOUND));
            everyTimeJob.toUpdateAll(updateJob);
            everyTimeJobRepository.save(everyTimeJob);
            everyTimeCnt++;
            //CustomizeJob 업데이트
        } else if (updateJob.getCompletion() == null){
            CustomizeJob customizeJob = customizeJobRepository.findById(updateJob.getId())
                    .orElseThrow(() -> new JobException(JobErrorCode.JOB_NOT_FOUND));
            customizeJob.toUpdateAll(updateJob);
            customizeJobRepository.save(customizeJob);
            customizeCnt++;

            //SeperatedJob 업데이트
        } else if (updateJob.getStartDate() == null){
            SeperatedJob seperatedJob = seperatedJobRepository.findById(updateJob.getId())
                    .orElseThrow(() -> new JobException(JobErrorCode.JOB_NOT_FOUND));
            seperatedJob.toUpdateAll(updateJob);
            seperatedJobRepository.save(seperatedJob);
            seperatedCnt++;
            nameofJob = seperatedJob.getName();
        }
        return nameofJob;
    }


}