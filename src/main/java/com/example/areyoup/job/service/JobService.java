package com.example.areyoup.job.service;

import com.example.areyoup.errors.errorcode.MemberErrorCode;
import com.example.areyoup.errors.exception.MemberException;
import com.example.areyoup.job.domain.BasicJob;
import com.example.areyoup.job.domain.CustomizeJob;
import com.example.areyoup.job.dto.JobRequestDto;
import com.example.areyoup.job.dto.JobResponseDto.CustomizeJobResponseDto;
import com.example.areyoup.job.dto.everytime.EverytimeRequestDto.EverytimeDto;
import com.example.areyoup.job.dto.everytime.EverytimeRequestDto.SubjectDto;
import com.example.areyoup.job.dto.everytime.EverytimeRequestDto.TimeLineDto;
import com.example.areyoup.job.repository.JobRepository;
import com.example.areyoup.member.Member;
import com.example.areyoup.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
//final 변수 생성자
@Slf4j
public class JobService {

    private final JobRepository jobRepository;
    private final MemberRepository memberRepository;

    /*
    everyTime 일정 저장
     */
    public String saveEveryTime(List<EverytimeDto> everytimeDtos, Long memberId) throws ParseException {
        Member m = memberRepository.findById(memberId)
                        .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        //member_id를 통해서 유저 확인 및 Job 데이터베이스에 JOIN
        saveTimeLine(everytimeDtos, m);
        return "Everytime schedule save successful";
    }

    /*
    everytime 시간표 json에서 일정들을 빼고, 일정 하나하나 DB에 저장하는 과정
    - everytime에 배치된 시간표는 요일만 확인해서 BasicJob으로 분류
     */
    private void saveTimeLine(List<EverytimeDto> everytimeDtos, Member member) throws ParseException {
        EverytimeDto everyTimeDto = everytimeDtos.get(everytimeDtos.size()-1); //마지막 시간표 가져오기
        //Todo 전체 시간표 가져오기? 마지막꺼만 가져오기? 확인
        List<TimeLineDto> timeLineDtos = everyTimeDto.getTimeline();

        //everytime 시간표 json에서 일정들을 빼고, 일정 하나하나 DB에 저장하는 과정
        for (TimeLineDto timeLineDto : timeLineDtos) {
            //한 요일마다 주어지는 일정들 빼기
            Integer dayOfTheWeek = Integer.valueOf(timeLineDto.getDay());
            List<SubjectDto> subject = timeLineDto.getSubject();
            for (SubjectDto everyTime : subject){

                //String -> LocalDate로 변환하여 소요 시간 계산
                String estimated_Time = cal_Time(everyTime.getStartTime(), everyTime.getEndTime());
                //Todo 소요 시간 계산이 잘 되는지 확인

                BasicJob job = BasicJob.builder()
                        .name(everyTime.getName())
                        .label(0)
                        .startTime(everyTime.getStartTime())
                        .endTime(everyTime.getEndTime())
                        .estimated_time(estimated_Time)
                        .member(member)
                        .dayOfTheWeek(dayOfTheWeek)
                        .build();
                //에브리타임 일정은 BasicJob으로 분류하여 생성
                jobRepository.save(job);
            }
        }
        log.info("에브리타임 일정 저장 성공");
    }

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
    public CustomizeJobResponseDto saveFixedJob(JobRequestDto.FixedJobRequestDto fixedJob) {
        CustomizeJob job = JobRequestDto.FixedJobRequestDto.toEntity(fixedJob);
        jobRepository.save(job);
        return CustomizeJobResponseDto.toDto(job);
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