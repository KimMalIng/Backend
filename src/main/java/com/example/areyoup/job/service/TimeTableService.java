package com.example.areyoup.job.service;

import com.example.areyoup.job.domain.BasicJob;
import com.example.areyoup.job.domain.CustomizeJob;
import com.example.areyoup.job.dto.JobRequestDto;
import com.example.areyoup.job.dto.JobResponseDto;
import com.example.areyoup.job.repository.BasicJobRepository;
import com.example.areyoup.job.repository.CustomizeJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TimeTableService {

    private final BasicJobRepository basicJobRepository;
    private final CustomizeJobRepository CustomizeJobRepository;

    /*
    시간 테이블 가져오기
     */
    public HashMap<String, List> getTable(JobRequestDto.PeriodRequestDto periodDto) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate start = LocalDate.parse(periodDto.getStartDate(), dtf);
        LocalDate end = LocalDate.parse(periodDto.getEndDate(), dtf);

        //날짜들의 요일에 해당되는 에타 시간표(Basic Jobs)를 가져온다.
        List<JobResponseDto.BasicJobResponseDto> basicJobs = getBasicJobs(start, end);

        //새로 배치된 Fixed Job을 가져오는 과정
        List<JobResponseDto.FixedJobResponseDto> fixedJobs = getCustomizeJobs(start, end);


        //todo 조정된 일정들 Seperated Job를 가져오는 과정
        HashMap<String, List> jobs = new HashMap<>();
        jobs.put("BasicJob", basicJobs);
        jobs.put("FixedJob", fixedJobs);
//        jobs.put("SeperatedJob", seperatedJobs);

        return jobs;
    }

    private List<JobResponseDto.FixedJobResponseDto> getCustomizeJobs(LocalDate start, LocalDate end) {
        List<CustomizeJob> customizeJobs =  CustomizeJobRepository.findByStartDateBetweenAndIsFixedIsTrue(start, end);
        List<JobResponseDto.FixedJobResponseDto> sjrd = new ArrayList<>();
        for (CustomizeJob customizeJob : customizeJobs){
            JobResponseDto.FixedJobResponseDto jobResponseDto = CustomizeJob.toCustomizeJobDto(customizeJob);
            sjrd.add(jobResponseDto);
        }
        return sjrd;
    }

    /*
    에브리타임에 고정된 일정들을 가져오는 단계
     */
    private List<JobResponseDto.BasicJobResponseDto> getBasicJobs(LocalDate start, LocalDate end) {
        //start ~ end 사이의 날짜들을 가져옴
        List<LocalDate> datesBetween = getAllDatesBetween(start, end);
        Set<Integer> dayOfWeeks = new HashSet<>(); //요일을 담을 Set
        for (LocalDate localDate : datesBetween){
            int dayOfWeek = localDate.getDayOfWeek().getValue()-1; //월요일이 1부터 시작
            //요일만 띄운다 -> 해당 기간 안에 필요한 요일만 넣어서 BasicJob 한번에 꺼내기
            dayOfWeeks.add(dayOfWeek);
        }
        List<BasicJob> basicJobs = basicJobRepository.findByDayOfTheWeekIn(dayOfWeeks);
        //기간 안의 요일들을 넣어서 그 요일에 해당하는 basicJob 반환
        List<JobResponseDto.BasicJobResponseDto> bjrd = new ArrayList<>();
        for (BasicJob basicJob : basicJobs){
            JobResponseDto.BasicJobResponseDto basicJobResponseDto = BasicJob.toBasicJobDto(basicJob);
            bjrd.add(basicJobResponseDto);
        }
        //basicJob들을 넣어서 Dto 형태로 반환
        return bjrd;
    }

    /*
    StartDate ~ endDate 의 날짜를 모두 가져오는 함수
     */
    private List<LocalDate> getAllDatesBetween(LocalDate start, LocalDate end) {
        List<LocalDate> localDates = new ArrayList<>();
        LocalDate startDate = start;
        while (!startDate.isAfter(end)){
            localDates.add(startDate);
            startDate = startDate.plusDays(1);
        }
        return localDates;
    }

}
