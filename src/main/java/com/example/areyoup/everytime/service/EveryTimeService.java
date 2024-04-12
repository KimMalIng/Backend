package com.example.areyoup.everytime.service;

import com.example.areyoup.errors.errorcode.MemberErrorCode;
import com.example.areyoup.errors.exception.MemberException;
import com.example.areyoup.everytime.domain.EveryTimeJob;
import com.example.areyoup.everytime.dto.EverytimeRequestDto;
import com.example.areyoup.global.function.CalTime;
import com.example.areyoup.job.repository.JobRepository;
import com.example.areyoup.member.domain.Member;
import com.example.areyoup.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EveryTimeService {

    private final MemberRepository memberRepository;
    private final JobRepository jobRepository;
    /*
    everyTime 일정 저장
     */
    public String saveEveryTime(List<EverytimeRequestDto.EverytimeDto> everytimeDtos, Long memberId) throws ParseException {
        Member m = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        //member_id를 통해서 유저 확인 및 Job 데이터베이스에 JOIN
        saveTimeLine(everytimeDtos, m);
        return "Everytime schedule save successful";
    }

    /*
    everytime 시간표 json에서 일정들을 빼고, 일정 하나하나 DB에 저장하는 과정
    - everytime에 배치된 시간표는 요일만 확인해서 EveryTimeJob으로 분류
     */
    private void saveTimeLine(List<EverytimeRequestDto.EverytimeDto> everytimeDtos, Member member) throws ParseException {
        EverytimeRequestDto.EverytimeDto everyTimeDto = everytimeDtos.get(everytimeDtos.size()-1); //마지막 시간표 가져오기
        List<EverytimeRequestDto.TimeLineDto> timeLineDtos = everyTimeDto.getTimeline();

        //everytime 시간표 json에서 일정들을 빼고, 일정 하나하나 DB에 저장하는 과정
        for (EverytimeRequestDto.TimeLineDto timeLineDto : timeLineDtos) {
            //한 요일마다 주어지는 일정들 빼기
            Integer dayOfTheWeek = Integer.valueOf(timeLineDto.getDay());
            List<EverytimeRequestDto.SubjectDto> subject = timeLineDto.getSubject();
            for (EverytimeRequestDto.SubjectDto everyTime : subject){

                //String -> LocalDate로 변환하여 소요 시간 계산
                String estimatedTime = CalTime.cal_Time(everyTime.getStartTime(), everyTime.getEndTime());

                EveryTimeJob job = EveryTimeJob.builder()
                        .name(everyTime.getName())
                        .label(0)
                        .startTime(everyTime.getStartTime())
                        .endTime(everyTime.getEndTime())
                        .estimatedTime(estimatedTime)
                        .isFixed(true)
                        .member(member)
                        .dayOfTheWeek(dayOfTheWeek)
                        .build();
                //에브리타임 일정은 EveryTimeJob으로 분류하여 생성
                jobRepository.save(job);
            }
        }
        log.info("에브리타임 일정 저장 성공");
    }

}
