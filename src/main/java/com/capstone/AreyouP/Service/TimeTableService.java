package com.capstone.AreyouP.Service;

import com.capstone.AreyouP.DTO.EveryTime.TimeLine;
import com.capstone.AreyouP.DTO.Schedule.AdjustmentDto;
import com.capstone.AreyouP.DTO.Schedule.JobDto;
import com.capstone.AreyouP.Domain.Job;
import com.capstone.AreyouP.Domain.TimeTable;
import com.capstone.AreyouP.Repository.TimeTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class TimeTableService {

    private final TimeTableRepository timeTableRepository;

    public List<TimeLine> getTable(String startDate, String endDate, Long user_id) {
        Date start = new Date();
        Date end = new Date();
        try {
            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
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
            SimpleDateFormat sdf  = new SimpleDateFormat("yyyyMMdd");
            String date = sdf.format(d);

            Job job = table.getJob();
            System.out.println(job);
            JobDto schedule = JobDto.builder()
                    .day(date)
                    .startTime(job.getStartTime())
                    .endTime(table.getJob().getEndTime())
                    .name(table.getJob().getName())
                    .deadline(table.getJob().getDeadLine())
                    .Estimated_time(table.getJob().getEstimated_Time())
                    .label(table.getJob().getLabel())
                    .build();
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

    public AdjustmentDto adjustSchedule(String startDate, String endDate, Long user_id) {
        Date start = new Date();
        Date end = new Date();
        try {
            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
            start = dayFormat.parse(startDate);
            end = dayFormat.parse(endDate);

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        List<TimeTable> timeTables = timeTableRepository.findAllByCalendarDateAndUserId(start, end, user_id);
        //내가 원하는 user에 대한 정보를 가져오려면 userId=? 인 것도 처리해주면 된다.

        AdjustmentDto timeLine = new AdjustmentDto();
        timeLine.setWeek_day(new ArrayList<>());
        timeLine.setSchedule(new ArrayList<>());
        Map<String, List<JobDto>> getJobsOfDay = new HashMap<>();

        for (TimeTable table : timeTables){
            Date d = table.getCalendar().getDate();
            SimpleDateFormat sdf  = new SimpleDateFormat("yyyyMMdd");
            String date = sdf.format(d);

            Job job = table.getJob();
            System.out.println(job);
            JobDto schedule = JobDto.builder()
                    .day(date)
                    .startTime(job.getStartTime())
                    .endTime(table.getJob().getEndTime())
                    .name(table.getJob().getName())
                    .deadline(table.getJob().getDeadLine())
                    .Estimated_time(table.getJob().getEstimated_Time())
                    .label(table.getJob().getLabel())
                    .build();
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
            timeLine.getWeek_day().add(entry.getKey());
            for (JobDto jobDto : entry.getValue()){
                timeLine.getSchedule().add(jobDto);
            }
        }

        return timeLine;
    }
}
