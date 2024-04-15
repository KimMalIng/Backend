package com.example.areyoup.timetable.service;

import com.example.areyoup.everytime.domain.EveryTimeJob;
import com.example.areyoup.everytime.dto.EveryTimeResponseDto;
import com.example.areyoup.global.function.DateTimeHandler;
import com.example.areyoup.job.domain.CustomizeJob;
import com.example.areyoup.job.domain.DefaultJob;
import com.example.areyoup.job.domain.SeperatedJob;
import com.example.areyoup.job.dto.JobRequestDto;
import com.example.areyoup.job.dto.JobResponseDto;
import com.example.areyoup.job.dto.JobResponseDto.ScheduleDto;
import com.example.areyoup.everytime.repository.EveryTimeJobRepository;
import com.example.areyoup.job.repository.CustomizeJobRepository;
import com.example.areyoup.job.repository.DefaultJobRepository;
import com.example.areyoup.job.repository.JobRepository;
import com.example.areyoup.job.repository.SeperatedJobRepository;
import com.example.areyoup.member.domain.Member;
import com.example.areyoup.member.service.MemberService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimeTableService {

    private static final String PATH = "..\\areyoup\\src\\main\\resources\\genetic_python\\";
    private final EveryTimeJobRepository everyTimeJobRepository;
    private final CustomizeJobRepository customizeJobRepository;
    private final SeperatedJobRepository seperatedJobRepository;
    private final DefaultJobRepository defaultJobRepository;
    private final JobRepository jobRepository;
    private final MemberService memberService;
    private final HttpServletRequest request;

    /*
    시간 테이블 가져오기
     */
    public HashMap<String, List> getTable(String startDate, String endDate) {
        Member m = memberService.findMember(request);
        LocalDate start = DateTimeHandler.strToDate(startDate);
        LocalDate end = DateTimeHandler.strToDate(endDate);

        //날짜들의 요일에 해당되는 에타 시간표(Basic Jobs)를 가져온다.
        List<EveryTimeResponseDto> EveryTimeJobs = getEveryTimeJobs(start, end, m.getId());

        //새로 배치된 Fixed Job을 가져오는 과정
        List<JobResponseDto.FixedJobResponseDto> fixedJobs = getCustomizeJobs(start, end, m.getId());

        List<JobResponseDto.SeperatedJobResponseDto> seperatedJobs = getSeperatedJobs(start,end, m.getId());

        HashMap<String, List> jobs = new HashMap<>();
        jobs.put("EveryTimeJob", EveryTimeJobs);
        jobs.put("FixedJob", fixedJobs);
        jobs.put("SeperatedJob", seperatedJobs);

        log.info("All schedule from {} to {} has been returned", start, end);

        return jobs;
    }

    private List<JobResponseDto.SeperatedJobResponseDto> getSeperatedJobs(LocalDate start, LocalDate end, Long memberId) {
        List<SeperatedJob> seperatedJobs =  seperatedJobRepository.findByDayBetweenAndIsFixedIsTrueAndMemberId(start, end,memberId);

        return seperatedJobs.stream()
                .map(SeperatedJob::toSeperatedJobDto)
                .toList();
    }

    private List<JobResponseDto.FixedJobResponseDto> getCustomizeJobs(LocalDate start, LocalDate end, Long memberId) {
        List<CustomizeJob> customizeJobs =  customizeJobRepository.findByStartDateBetweenAndIsFixedIsTrue(start, end, memberId);

        return customizeJobs.stream()
                .map(CustomizeJob::toCustomizeJobDto)
                .toList();
    }

    /*
    에브리타임에 고정된 일정들을 가져오는 단계
     */
    private List<EveryTimeResponseDto> getEveryTimeJobs(LocalDate start, LocalDate end, Long memberId) {
        //start ~ end 사이의 날짜들을 가져옴
        List<LocalDate> datesBetween = getAllDatesBetween(start, end);
        Set<Integer> dayOfWeeks = new HashSet<>(); //요일을 담을 Set
        for (LocalDate localDate : datesBetween){
            int dayOfWeek = localDate.getDayOfWeek().getValue()-1; //월요일이 1부터 시작
            //요일만 띄운다 -> 해당 기간 안에 필요한 요일만 넣어서 EveryTimeJob 한번에 꺼내기
            dayOfWeeks.add(dayOfWeek);
        }
        List<EveryTimeJob> everyTimeJobs = everyTimeJobRepository.findByDayOfTheWeekInAndMemberId(dayOfWeeks, memberId);
        //기간 안의 요일들을 넣어서 그 요일에 해당하는 EveryTimeJob 반환
        //EveryTimeJob들을 넣어서 Dto 형태로 반환
        return everyTimeJobs.stream()
                .map(EveryTimeJob::toEveryTimeJobDto)
                .toList();
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

    /*
    일정 조정 함수
     */
    public JobResponseDto.AdjustmentDto adjustSchedule(JobRequestDto.PeriodRequestDto periodDto) throws IOException {
        JobResponseDto.AdjustmentDto timeLine = new JobResponseDto.AdjustmentDto();
        String startDate = periodDto.getStartDate(); //yyyy.MM.dd
        String endDate = periodDto.getEndDate();
        Long memberId = memberService.findMember(request).getId();

        LocalDateTime now = LocalDateTime.now(); //현재 날짜와 시간 가져오기
        LocalDate start = DateTimeHandler.strToDate(startDate);
        LocalDate end = DateTimeHandler.strToDate(endDate);
        //시작, 끝 날짜 formatting 과정

        //1. 스케줄 시작 시간 세팅
        if (now.toLocalDate().equals(start)){ //시작하는 날짜와 현재 날짜가 같다면
            timeLine.setSchedule_startTime(String.valueOf(now.toLocalTime()).substring(0,5));
            //조정하는 현 시각부터 스케줄링 시작
        } else { //다르다면 시작하는 날짜의 00시부터 시작
            timeLine.setSchedule_startTime("00:00");
        }

        //2. 날짜 세팅
        List<LocalDate> datesBetween = getAllDatesBetween(start, end);
        List<String> days = datesBetween.stream()
                .map(DateTimeHandler::dateToStr)
                .collect(Collectors.toList());
        timeLine.setWeek_day(days);

        //3. 기본적인 일정 가져오기
        //기본 일정 DefaultJob 반환 (취침, 아침, 점심, 저녁)
        List<DefaultJob> defaultJobs = defaultJobRepository.findAllByMemberId(memberId);
        List<JobResponseDto.DefaultJobResponseDto> defaultJob = defaultJobs.stream()
                .map(JobResponseDto.DefaultJobResponseDto::toResponseDto)
                .toList();
        timeLine.setDefaultJobs(defaultJob);

        //주어진 기간안에 일정들 가져오기
        List<ScheduleDto> adjustJobs = getAdjustJobs(start, end, datesBetween, memberId);
        timeLine.setSchedule(adjustJobs); //스케줄 세팅

        saveFile(timeLine); //data.json에 저장

        return genetic(memberId);

    }

    /*
    유전 알고리즘 실행하여 조정된 일정 저장
     */
    private JobResponseDto.AdjustmentDto genetic(Long memberId) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", PATH+ "Scheduling_Algorithm_v2.py");

            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            log.info("Exited with error code " + exitCode);
            //python 파일 실행
            log.info("python build");
            Thread.sleep(500);
            FileReader fr = new FileReader(PATH + "data.json");
            JSONParser parser = new JSONParser(fr);
            ObjectMapper mapper = new ObjectMapper();
            Object obj = parser.parse();
            fr.close();
            String jsonStr = mapper.writeValueAsString(obj);
            JobResponseDto.AdjustmentDto adjustmentDto = mapper.readValue(jsonStr, JobResponseDto.AdjustmentDto.class);

            //data.json에서 가져와서 adjustmentdto에 넣어주는 과정

            if (exitCode == 0){
                List<ScheduleDto> replace = new ArrayList<>();
                //label !=0 이라면 조정된 것들
                for (ScheduleDto scheduleDto : adjustmentDto.getSchedule()) {
                    if (scheduleDto.getLabel() != 0){
                        LocalDate day = DateTimeHandler.strToDate(scheduleDto.getDay());
                        JobResponseDto.SeperatedJobResponseDto responseDto = JobResponseDto.SeperatedJobResponseDto.toSeperatedJob(scheduleDto);
                        SeperatedJob seperatedJob = JobResponseDto.SeperatedJobResponseDto.toEntity(responseDto, memberService.findMember(request));
                        seperatedJobRepository.save(seperatedJob);
                        replace.add(ScheduleDto.toScheduleDto(seperatedJobRepository.findByDayAndStartTimeAndMemberId(day, scheduleDto.getStartTime(), memberId)));
                        //일정에 대한 id 값을 넘겨주기 위해 repository에서 다시 꺼내와서 넣기

                        CustomizeJob customizeJob = customizeJobRepository.findByNameAndMemberId(scheduleDto.getName(), memberId);
                        customizeJob.toFixUpdate(false);
                    } else {
                        replace.add(scheduleDto);
                    }
                }
                adjustmentDto.setSchedule(replace); //대체한 scheduleDtofh 처리한다
                log.info("Success save seperatedJobs");
            } else if (exitCode == 1) {
                log.warn("There's no adjustJob");
            }else if (exitCode == 2) {
                log.warn("Path Error");
            }

            return adjustmentDto;

        } catch (IOException e) {
            log.error("IOException " + e.getMessage());
        } catch (ParseException | InterruptedException e) {
            log.error("python 실행 및 json 에러", e.getMessage());
        }
        return null; //todo null 처리
    }

    /*
    data.json 파일에 저장
     */
    private void saveFile(JobResponseDto.AdjustmentDto timeLine){
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("week_day", timeLine.getWeek_day());
            hashMap.put("schedule_startTime", timeLine.getSchedule_startTime());
            hashMap.put("schedule", timeLine.getSchedule());
            hashMap.put("defaultJobs", timeLine.getDefaultJobs());

            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
            String jsonString = mapper.writeValueAsString(hashMap);

            FileWriter file = new FileWriter(PATH + "data.json");
            file.write(jsonString);
            file.flush();
            file.close();

            log.info("Save File in {}data.json", PATH);
        }
        catch (IOException e){
            log.error("IOException", e.getMessage());
        }
    }

    /*
    조정할 때 필요한 일정들을 모두 가져옴
     */
    private List<ScheduleDto> getAdjustJobs(LocalDate start, LocalDate end, List<LocalDate> datesBetween, Long memberId) {
        List<ScheduleDto> result = new ArrayList<>();
        for (LocalDate localDate : datesBetween){
            int dayOfWeek = localDate.getDayOfWeek().getValue()-1; //월요일 1부터 시작해서 -1 처리
            //해당 요일에 해당하는 EveryTimeJob들 가져오기 (하루에 듣는 수업이 여러 개 일수도 있음)
            List<EveryTimeJob> everyTimeJob = everyTimeJobRepository.findByDayOfTheWeekAndMemberId(dayOfWeek, memberId);
            //todo memberId까지 확인해야함
            for (EveryTimeJob basic : everyTimeJob){
                ScheduleDto b = ScheduleDto.toScheduleDto(basic, localDate);
                result.add(b);
                //해당 요일에 해당하는 날짜 넣어서 반환
            }
        }

        //기간 안에 존재하는 고정된 일정 customJob 반환
        List<CustomizeJob> fixedJobs = customizeJobRepository.findFixedJob(start, end, memberId);
        List<ScheduleDto> fixed = fixedJobs.stream()
                .map(ScheduleDto::toScheduleDto)
                .toList();

        //시작 시간이 null, 고정 X 일정 -> 조정해야 하는 일정들
        //todo 조정해야 하는 일정들은 id 값으로 프론트에서 넘겨받기?
        List<CustomizeJob> adjustJobs = customizeJobRepository.findAdjustJob(memberId);
        List<ScheduleDto> adjust = adjustJobs.stream()
                .map(ScheduleDto::toScheduleDto)
                .toList();

        //조정된 일정 중 고정된 일정
        List<SeperatedJob> seperatedJobs = seperatedJobRepository.findByDayBetweenAndIsFixedIsTrueAndMemberId(start, end, memberId);
        List<ScheduleDto> seperated = seperatedJobs.stream()
                .map(ScheduleDto::toScheduleDto)
                .toList();

        result.addAll(fixed);
        result.addAll(adjust);
        result.addAll(seperated);

        return result;
    }

    public String calLeftTime(String startDate, String endDate) {
        Member m = memberService.findMember(request);
        LocalDate start = DateTimeHandler.strToDate(startDate);
        LocalDate end = DateTimeHandler.strToDate(endDate);

        long defaultMinute = getTimeOfDefaultJob(start, end, m.getId());
        Integer everyTimeMinute = getTimeOfEveryTimeJob(start,end, m.getId());
        Integer customizeMinute = getTimeOfCustomizeJob(start,end, m.getId());
        Integer seperatedMinute = getTimeOfSeperatedJob(start,end, m.getId());
        long totalMinute = defaultMinute + everyTimeMinute + customizeMinute + seperatedMinute;
        //기간 내의 defaultJob + everyTimeJob + customizeJob + seperatedJob 총 소요시간

        log.info("기간 내의 총 소요 시간 : {}, {}분",
                String.format("%02d:%02d", (totalMinute/60),(totalMinute%60)),
                totalMinute
        );
        LocalDateTime s = start.atStartOfDay();
        LocalDateTime e = end.plusDays(1).atStartOfDay();
        Duration duration = Duration.between(s, e);

        int mintuesPeriod = (int) duration.toMinutes();

        log.info("기간 내의 총 시간 : {}",
                String.format("%02d:%02d", (mintuesPeriod/60),(mintuesPeriod%60)),
                mintuesPeriod
        );

        int result = (int) (mintuesPeriod - totalMinute);

        return String.format("%02d:%02d", (result/60),(result%60));
    }

    private long getTimeOfDefaultJob(LocalDate start, LocalDate end, Long id) {

        long days = ChronoUnit.DAYS.between(start,end) + 1;
        long result = jobRepository.getLeftTimeFromDefaultJob(id) * days;
        log.info("DefaultJob의 총 소요시간 : {}",String.format("%02d:%02d", (result/60),(result%60)));
        return result;
    }

    private Integer getTimeOfSeperatedJob(LocalDate start, LocalDate end, Long id) {
        Integer result = 0 ;
        List<SeperatedJob> seperatedJobs = seperatedJobRepository.findByDayBetweenAndIsFixedIsTrueAndMemberId(start,end, id);
        for (SeperatedJob basic : seperatedJobs){
            LocalTime time = DateTimeHandler.strToTime(basic.getEstimatedTime());
            int calTime = (time.getHour() * 60 + time.getMinute());
            result += calTime;
        }
        log.info("SeperatedJob의 총 소요시간 : {}", String.format("%02d:%02d", (result/60),(result%60)));
        return result;
    }

    private Integer getTimeOfCustomizeJob(LocalDate start, LocalDate end, Long id) {
        Integer result = 0;
        List<CustomizeJob> customizeJobs = customizeJobRepository.findFixedJob(start, end, id);
        for (CustomizeJob basic : customizeJobs){
            LocalTime time = DateTimeHandler.strToTime(basic.getEstimatedTime());
            int calTime = (time.getHour() * 60 + time.getMinute());
            result += calTime;
        }
        log.info("FixedJob의 총 소요시간 : {}", String.format("%02d:%02d", (result/60),(result%60)));
        return result;
    }

    //기간 안의 EveryTimeJob 총 소요시간
    private Integer getTimeOfEveryTimeJob(LocalDate start, LocalDate end, Long id) {
        Integer result = 0;
        List<LocalDate> datesBetween = getAllDatesBetween(start, end);
        for (LocalDate localDate : datesBetween){
            int dayOfWeek = localDate.getDayOfWeek().getValue()-1; //월요일 1부터 시작해서 -1 처리
            //해당 요일에 해당하는 EveryTimeJob들 가져오기 (하루에 듣는 수업이 여러 개 일수도 있음)
            List<EveryTimeJob> everyTimeJob = everyTimeJobRepository.findByDayOfTheWeekAndMemberId(dayOfWeek, id);
            for (EveryTimeJob basic : everyTimeJob){
                LocalTime time = DateTimeHandler.strToTime(basic.getEstimatedTime());
                int calTime = (time.getHour() * 60 + time.getMinute());
                result += calTime;
                //해당 일정의 소요시간 더하는 과정
            }
        }
        log.info("EveryTimeJob의 총 소요시간 : {} ",String.format("%02d:%02d", (result/60),(result%60)));
        return result;
    }


}
