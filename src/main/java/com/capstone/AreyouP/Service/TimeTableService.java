package com.capstone.AreyouP.Service;

import com.capstone.AreyouP.DTO.EveryTime.EveryTimeDto;
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

//    public Object save(EveryTimeDto everyTimeDto) {
//
//    }

    public Map<String,List<Job>> getTable(String startDate, String endDate) {
        Date start = new Date();
        Date end = new Date();
        try {
            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
            start = dayFormat.parse(startDate);
            end = dayFormat.parse(endDate);

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
        List<TimeTable> timeTables = timeTableRepository.findAllByCalendarDate(start, end);
        //내가 원하는 user에 대한 정보를 가져오려면 userId=? 인 것도 처리해주면 된다.

        Map<String, List<Job>> getJobsOfDay = new HashMap<>();
        for (TimeTable table : timeTables){
            String date = table.getCalendar().getDate().toString();
            Job job = table.getJob();
            if (getJobsOfDay.containsKey(date)){
                Job j = table.getJob();
                getJobsOfDay.get(date).add(j);
            } else{
                List<Job> jobs = new ArrayList<>();
                jobs.add(job);
                getJobsOfDay.put(date, jobs);
            }
        }

        return getJobsOfDay;

    }
}
