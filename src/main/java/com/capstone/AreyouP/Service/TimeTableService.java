package com.capstone.AreyouP.Service;

import com.capstone.AreyouP.DTO.EveryTime.TimeLine;
import com.capstone.AreyouP.DTO.Schedule.AdjustmentDto;
import com.capstone.AreyouP.DTO.Schedule.JobDto;
import com.capstone.AreyouP.DTO.Schedule.PeriodDto;
import com.capstone.AreyouP.Domain.Calendar;
import com.capstone.AreyouP.Domain.Job;
import com.capstone.AreyouP.Domain.SeperatedJob;
import com.capstone.AreyouP.Domain.TimeTable;
import com.capstone.AreyouP.Repository.*;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONArray;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.json.JSONObject;

@Service
@RequiredArgsConstructor
public class TimeTableService {

    private static final String PATH = "C:\\Users\\minky\\Desktop\\민경\\졸업작품\\AreyouP\\src\\main\\resources\\genetic_python\\";
    private final TimeTableRepository timeTableRepository;
    private final SeperatedJobRepository seperatedJobRepository;
    private final JobRepository jobRepository;
    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;

    public void saveFile(AdjustmentDto adjustmentDto) throws IOException {
        HashMap <String, Object> hashMap = new HashMap<>();
        hashMap.put("week_day", adjustmentDto.getWeek_day());
        hashMap.put("schedule_startTime", adjustmentDto.getSchedule_startTime());
        hashMap.put("schedule", adjustmentDto.getSchedule());

        JSONObject jsonObject = new JSONObject(hashMap);

        FileWriter file = new FileWriter(PATH+"data.json");
        file.write(jsonObject.toString());
        file.flush();
        file.close();

    }

    public AdjustmentDto genetic() throws FileNotFoundException {
        AdjustmentDto adjustmentDto = new AdjustmentDto();
        adjustmentDto.setWeek_day(new ArrayList<>());
        adjustmentDto.setSchedule(new ArrayList<>());



        try{
            Process process = Runtime.getRuntime().exec("python "+PATH+"genetic_algorithm.py");
            Thread.sleep(1000);
            FileReader fr = new FileReader(PATH+"data.json");
            JSONParser parser = new JSONParser(fr);
            ObjectMapper mapper = new ObjectMapper();
            Object obj = parser.parse();

            fr.close();

            String jsonStr = mapper.writeValueAsString(obj);

            adjustmentDto = mapper.readValue(jsonStr, AdjustmentDto.class);

                // Accessing schedule items
                for (JobDto scheduleItem : adjustmentDto.getSchedule()) {
//                    if (scheduleItem instanceof JSONObject) {

                        int label = scheduleItem.getLabel();

                        SeperatedJob seperatedJob = new SeperatedJob();
                        Long user_id = null;

                        if (label != 0) {
                            Optional<Job> j = jobRepository.findById(scheduleItem.getJob_id());
//                            Job bigJob = new Job();
                            if (j.isPresent()){
//                                bigJob = j.get();
                                user_id = j.get().getTimeTables().get(0).getUser().getId();
                            }
                            seperatedJob = SeperatedJob.builder()
                                    .job_id(scheduleItem.getJob_id())
                                    .startTime(scheduleItem.getStartTime())
                                    .endTime(scheduleItem.getEndTime())
                                    .name(scheduleItem.getName())
                                    .day(scheduleItem.getDay())
                                    .label(scheduleItem.getLabel())
                                    .build();
                            seperatedJobRepository.save(seperatedJob);

                            SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
                            Date date = format.parse(scheduleItem.getDay());
                            Calendar c = calendarRepository.findByDate(date);
                            if (c==null){
                                java.util.Calendar cal = java.util.Calendar.getInstance();
                                cal.setTime(date);
                                int dow = cal.get(java.util.Calendar.DAY_OF_WEEK)-1;
                                if (dow==-1) dow=6;
                                boolean h = false;
                                if (dow==0 || dow == 6) h=true;


                                c = Calendar.builder()
                                        .month(cal.get(java.util.Calendar.MONTH) + 1)
                                        .date(date)
                                        .week(cal.get(java.util.Calendar.WEEK_OF_MONTH))
                                        .year(cal.get(java.util.Calendar.YEAR))
                                        .dayOfWeek(String.valueOf(dow))
                                        .Holiday(h)
                                        .build();
                                calendarRepository.save(c);
                            }
                            System.out.println(c);
                            TimeTable t = TimeTable.builder()
                                    .calendar(c)
                                    .seperatedJob(seperatedJob)
                                    .user(userRepository.findById(user_id).orElseThrow(
                                            ()-> new EntityNotFoundException("사용자를 찾을 수 없습니다.")))
                                    .build();

                            timeTableRepository.save(t);
                        }
                    }


        } catch (IOException e){
            e.printStackTrace();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (java.text.ParseException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return adjustmentDto;
    }

    public List<TimeLine> getTable(String startDate, String endDate, Long user_id){
        Date start = new Date();
        Date end = new Date();
        try {
            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy.MM.dd");
            start = dayFormat.parse(startDate);
            end = dayFormat.parse(endDate);

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
            List<TimeTable> timeTables = timeTableRepository.findAllByCalendarDateAndUserId(start, end, user_id);
        //내가 원하는 user에 대한 정보를 가져오려면 userId=? 인 것도 처리해주면 된다.

        List<TimeLine> timeLines = new ArrayList<>();

        Map<String, List<JobDto>> getJobsOfDay = new HashMap<>();
        for (TimeTable table : timeTables){
            Date d = table.getCalendar().getDate();
            SimpleDateFormat sdf  = new SimpleDateFormat("yyyy.MM.dd");
            String date = sdf.format(d);

            JobDto schedule = new JobDto();
            Job job = table.getJob();
            if (job==null){
                SeperatedJob seperatedJob = table.getSeperatedJob();
                schedule = JobDto.builder()
                        .day(seperatedJob.getDay())
                        .startTime(seperatedJob.getStartTime())
                        .endTime(seperatedJob.getEndTime())
                        .name(seperatedJob.getName())
                        .deadline((seperatedJob.getDeadline()))
                        .label(seperatedJob.getLabel())
                        .isPrivate(seperatedJob.isPrivate())
                        .build();
            } else {
                System.out.println(job);
                schedule = JobDto.builder()
                        .day(date)
                        .startTime(job.getStartTime())
                        .endTime(table.getJob().getEndTime())
                        .name(table.getJob().getName())
                        .deadline(String.valueOf(table.getJob().getDeadline()))
                        .estimated_time(table.getJob().getEstimated_time())
                        .label(table.getJob().getLabel())
                        .build();
            }
            if (getJobsOfDay.containsKey(date)){
                getJobsOfDay.get(date).add(schedule);
            } else{
                List<JobDto> jobDtos = new ArrayList<>();
                jobDtos.add(schedule);
                getJobsOfDay.put(date, jobDtos);
            }

        }
        System.out.println(getJobsOfDay);
        for (Map.Entry<String, List<JobDto>> entry : getJobsOfDay.entrySet()){
            TimeLine timeLine = new TimeLine();
            timeLine.setDay(entry.getKey());
            timeLine.setSubject(entry.getValue());
            timeLines.add(timeLine);
        }

        return timeLines;

    }

    public AdjustmentDto adjustSchedule(PeriodDto periodDto) throws IOException {
        String startDate = periodDto.getStartDate();;
        String endDate = periodDto.getEndDate();
        Long user_id = periodDto.getUser_id();
        //DTO에 저장된 내용들 반환
        LocalTime now = LocalTime.now();
        String schedule_startTime = String.valueOf(now).substring(0,5);
        //현재 조정을 하는 시간 저장
        Date start = new Date();
        Date end = new Date();
        try {
            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy.MM.dd");
            start = dayFormat.parse(startDate);
            end = dayFormat.parse(endDate);

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        List<TimeTable> timeTables = timeTableRepository.findAllByCalendarDateAndUserId(start, end, user_id);
        //user에 대해 원하는 시작 날짜 ~ 끝 날짜에 대한 타임테이블을 가져온다.

        AdjustmentDto timeLine = new AdjustmentDto();
        timeLine.setWeek_day(new ArrayList<>());
        timeLine.setSchedule(new ArrayList<>());
        timeLine.setSchedule_startTime(schedule_startTime);
        //초기화

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

        LocalDate startLocalDate = LocalDate.parse(startDate, formatter);
        LocalDate endLocalDate = LocalDate.parse(endDate, formatter);

        while (!startLocalDate.isAfter(endLocalDate)) {
            timeLine.getWeek_day().add(startLocalDate.format(formatter));
            startLocalDate = startLocalDate.plusDays(1);
        }



        for (TimeTable table : timeTables){
            Date d = table.getCalendar().getDate();
            SimpleDateFormat sdf  = new SimpleDateFormat("yyyy.MM.dd");
            String date = sdf.format(d);

            Job job = table.getJob();
            System.out.println(job);
            if (job==null){
                long table_id = table.getId();
                long seperated_id = table.getSeperatedJob().getId();
                seperatedJobRepository.deleteById(seperated_id);
                timeTableRepository.deleteById(table_id);
            } else {
                JobDto schedule = JobDto.builder()
                        .user_id(table.getUser().getId())
                        .job_id(job.getId())
                        .day(date)
                        .startTime(job.getStartTime())
                        .endTime(table.getJob().getEndTime())
                        .name(table.getJob().getName())
                        .deadline(String.valueOf(table.getJob().getDeadline()))
                        .estimated_time(table.getJob().getEstimated_time())
                        .label(table.getJob().getLabel())
                        .build();
                timeLine.getSchedule().add(schedule);
            }
        }

        List<TimeTable> needToAdjust = timeTableRepository.findAllByUserIdAndJobIsNOTCompleteAndJobLabelISNOTZERO(user_id);

        for (TimeTable table : needToAdjust){

            Job job = table.getJob();
            System.out.println(job);
            JobDto schedule = JobDto.builder()
                    .user_id(table.getUser().getId())
                    .job_id(job.getId())
                    .startTime(job.getStartTime())
                    .endTime(table.getJob().getEndTime())
                    .name(table.getJob().getName())
                    .deadline(String.valueOf(table.getJob().getDeadline()))
                    .estimated_time(table.getJob().getEstimated_time())
                    .label(table.getJob().getLabel())
                    .build();
            timeLine.getSchedule().add(schedule);
        }

        saveFile(timeLine); //data.json 파일에 저장

        return genetic();
    }
}
