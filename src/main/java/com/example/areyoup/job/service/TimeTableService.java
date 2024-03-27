package com.example.areyoup.job.service;

import com.example.areyoup.everytime.domain.EveryTimeJob;
import com.example.areyoup.everytime.dto.EveryTimeResponseDto;
import com.example.areyoup.job.domain.CustomizeJob;
import com.example.areyoup.job.domain.SeperatedJob;
import com.example.areyoup.job.dto.JobRequestDto;
import com.example.areyoup.job.dto.JobResponseDto;
import com.example.areyoup.job.dto.JobResponseDto.ScheduleDto;
import com.example.areyoup.everytime.repository.EveryTimeJobRepository;
import com.example.areyoup.job.repository.CustomizeJobRepository;
import com.example.areyoup.job.repository.SeperatedJobRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TimeTableService {

    private static final String PATH = "..\\areyoup\\src\\main\\resources\\genetic_python\\";
    private final EveryTimeJobRepository everyTimeJobRepository;
    private final CustomizeJobRepository CustomizeJobRepository;
    private final SeperatedJobRepository seperatedJobRepository;

    /*
    시간 테이블 가져오기
     */
    public HashMap<String, List> getTable(JobRequestDto.PeriodRequestDto periodDto) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate start = LocalDate.parse(periodDto.getStartDate(), dtf);
        LocalDate end = LocalDate.parse(periodDto.getEndDate(), dtf);

        //날짜들의 요일에 해당되는 에타 시간표(Basic Jobs)를 가져온다.
        List<EveryTimeResponseDto> EveryTimeJobs = getEveryTimeJobs(start, end);

        //새로 배치된 Fixed Job을 가져오는 과정
        List<JobResponseDto.FixedJobResponseDto> fixedJobs = getCustomizeJobs(start, end);

        List<JobResponseDto.SeperatedJobResponseDto> seperatedJobs = getSeperatedJobs(start,end);

        HashMap<String, List> jobs = new HashMap<>();
        jobs.put("EveryTimeJob", EveryTimeJobs);
        jobs.put("FixedJob", fixedJobs);
        jobs.put("SeperatedJob", seperatedJobs);

        return jobs;
    }

    private List<JobResponseDto.SeperatedJobResponseDto> getSeperatedJobs(LocalDate start, LocalDate end) {
        List<SeperatedJob> seperatedJobs =  seperatedJobRepository.findByDayBetweenAndIsFixedIsTrue(start, end);

        return seperatedJobs.stream()
                .map(SeperatedJob::toSeperatedJobDto)
                .toList();
    }

    private List<JobResponseDto.FixedJobResponseDto> getCustomizeJobs(LocalDate start, LocalDate end) {
        List<CustomizeJob> customizeJobs =  CustomizeJobRepository.findByStartDateBetweenAndIsFixedIsTrue(start, end);

        return customizeJobs.stream()
                .map(CustomizeJob::toCustomizeJobDto)
                .toList();
    }

    /*
    에브리타임에 고정된 일정들을 가져오는 단계
     */
    private List<EveryTimeResponseDto> getEveryTimeJobs(LocalDate start, LocalDate end) {
        //start ~ end 사이의 날짜들을 가져옴
        List<LocalDate> datesBetween = getAllDatesBetween(start, end);
        Set<Integer> dayOfWeeks = new HashSet<>(); //요일을 담을 Set
        for (LocalDate localDate : datesBetween){
            int dayOfWeek = localDate.getDayOfWeek().getValue()-1; //월요일이 1부터 시작
            //요일만 띄운다 -> 해당 기간 안에 필요한 요일만 넣어서 EveryTimeJob 한번에 꺼내기
            dayOfWeeks.add(dayOfWeek);
        }
        List<EveryTimeJob> everyTimeJobs = everyTimeJobRepository.findByDayOfTheWeekIn(dayOfWeeks);
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
        Long memberId = periodDto.getMemberId();
        //todo memberId 사용...

        LocalDateTime now = LocalDateTime.now(); //현재 날짜와 시간 가져오기
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDate start = LocalDate.parse(startDate, dtf);
        LocalDate end = LocalDate.parse(endDate, dtf);
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
                .map(localDate -> localDate.toString().replace("-", "."))
                .collect(Collectors.toList());
        timeLine.setWeek_day(days);


        //주어진 기간안에 일정들 가져오기
        List<ScheduleDto> adjustJobs = getAdjustJobs(start, end, datesBetween);
        timeLine.setSchedule(adjustJobs); //스케줄 세팅

        System.out.println(timeLine);
        saveFile(timeLine); //data.json에 저장

        return genetic();

    }

    /*
    유전 알고리즘 실행하여 조정된 일정 저장
     */
    private JobResponseDto.AdjustmentDto genetic() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", PATH+ "genetic_algorithm.py");

            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            System.out.println("Exited with error code " + exitCode);
            //python 파일 실행
            Thread.sleep(500);
            FileReader fr = new FileReader(PATH + "data.json");
            JSONParser parser = new JSONParser(fr);
            ObjectMapper mapper = new ObjectMapper();
            Object obj = parser.parse();
            fr.close();
            String jsonStr = mapper.writeValueAsString(obj);
            JobResponseDto.AdjustmentDto adjustmentDto = mapper.readValue(jsonStr, JobResponseDto.AdjustmentDto.class);

            //data.json에서 가져와서 adjustmentdto에 넣어주는 과정

            //label !=0 이라면 조정된 것들
            for (ScheduleDto scheduleDto : adjustmentDto.getSchedule()) {
                if (scheduleDto.getLabel() != 0){
                    JobResponseDto.SeperatedJobResponseDto responseDto = JobResponseDto.SeperatedJobResponseDto.toSeperatedJob(scheduleDto);
                    SeperatedJob seperatedJob = JobResponseDto.SeperatedJobResponseDto.toEntity(responseDto);
                    seperatedJobRepository.save(seperatedJob);
                }
            }

            return adjustmentDto;
        } catch (IOException e) {
            log.error("IOException " + e.getMessage());
        } catch (ParseException | InterruptedException e) {
            log.error("python 실행 및 json 에러", e.getMessage());
        }
        return null; //todo null 처리
    }

    private void saveFile(JobResponseDto.AdjustmentDto timeLine) throws IOException {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("week_day", timeLine.getWeek_day());
        hashMap.put("schedule_startTime", timeLine.getSchedule_startTime());
        hashMap.put("schedule", timeLine.getSchedule());

        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        String jsonString = mapper.writeValueAsString(hashMap);

//        JSONObject jsonObject = new JSONObject(hashMap);

        FileWriter file = new FileWriter(PATH+"data.json");
        file.write(jsonString);
        file.flush();
        file.close();
    }

    /*
    조정할 때 필요한 일정들을 모두 가져옴
     */
    private List<ScheduleDto> getAdjustJobs(LocalDate start, LocalDate end, List<LocalDate> datesBetween) {
        List<ScheduleDto> result = new ArrayList<>();
        for (LocalDate localDate : datesBetween){
            int dayOfWeek = localDate.getDayOfWeek().getValue()-1; //월요일이 1부터 시작
            //요일만 띄운다 -> 해당 기간 안에 필요한 요일만 넣어서 EveryTimeJob 한번에 꺼내기
            List<EveryTimeJob> everyTimeJob = everyTimeJobRepository.findByDayOfTheWeek(dayOfWeek);
            //todo memberId까지 확인해야함
            for (EveryTimeJob basic : everyTimeJob){
                ScheduleDto b = ScheduleDto.toScheduleDto(basic, localDate);
                result.add(b);
                //해당 요일에 해당하는 날짜 넣어서 반환
            }
        }

        //기간 안에 존재하는 고정된 일정 customJob 반환
        List<CustomizeJob> fixedJobs = CustomizeJobRepository.findByStartDateBetweenAndIsFixedIsTrue(start, end);
        List<ScheduleDto> fixed = fixedJobs.stream()
                .map(ScheduleDto::toScheduleDto)
                .toList();

        //시작 시간이 null 인 일정 -> 조정해야 하는 일정들
        //todo 조정해야 하는 일정들은 id 값으로 프론트에서 넘겨받기?
        List<CustomizeJob> adjustJobs = CustomizeJobRepository.findByStartTimeIsNull();
        List<ScheduleDto> adjust = adjustJobs.stream()
                .map(ScheduleDto::toScheduleDto)
                .toList();

        //조정된 일정 중 고정된 일정
        List<SeperatedJob> seperatedJobs = seperatedJobRepository.findByDayBetweenAndIsFixedIsTrue(start, end);
        List<ScheduleDto> seperated = seperatedJobs.stream()
                .map(ScheduleDto::toScheduleDto)
                .toList();

        result.addAll(fixed);
        result.addAll(adjust);
        result.addAll(seperated);

        return result;
    }
}
