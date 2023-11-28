package com.capstone.AreyouP.Service;

import com.capstone.AreyouP.DTO.EveryTime.EveryTimeDto;
import com.capstone.AreyouP.DTO.EveryTime.TimeLine;
import com.capstone.AreyouP.DTO.ScheduleDto;
import com.capstone.AreyouP.Domain.Calendar;
import com.capstone.AreyouP.Domain.Job;
import com.capstone.AreyouP.Domain.TimeTable;
import com.capstone.AreyouP.Repository.CalendarRepository;
import com.capstone.AreyouP.Repository.JobRepository;
import com.capstone.AreyouP.Repository.TimeTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.*;
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final CalendarRepository calendarRepository;
    private final TimeTableRepository timeTableRepository;

    public List<Job> saveEveryTime(List<EveryTimeDto> everyTimeDtos) {

        int year=0, semester = 0, day=0;
        Job everyTimeJob = new Job();
        Calendar calendar = new Calendar();
        EveryTimeDto everyTimeDto = everyTimeDtos.get(everyTimeDtos.size()-1);
        year = everyTimeDto.getYear();
        semester = Integer.parseInt(everyTimeDto.getSemester());
        List<TimeLine> timeLineList = everyTimeDto.getTimeline();
        List<Job> everyTimeJobs = new ArrayList<>();


        //캘린더, 유저, job 정보를 timetable repository에 저장
        //year, semester에 따라 3.2 - 6.14 9.1 - 12.14 판단 가능, day에 따라 해당 날짜 중 같은 요일에 저장될 수 있도록
        //user 정보는 아직 ,, calendar에 잘 들어가냐만 판단해보잣!

        for (TimeLine timeLine : timeLineList) {


            List<ScheduleDto> subject = timeLine.getSubject();
            for (ScheduleDto everyTime : subject) {
                everyTimeJob = Job.builder()
                        .startTime(everyTime.getStartTime())
                        .endTime(everyTime.getEndTime())
                        .jobName(everyTime.getName())
                        .label(0)
                        .build();
                everyTimeJobs.add(everyTimeJob);
                jobRepository.save(everyTimeJob);
            }

            day = timeLine.getDay();
            int dayOfWeekNum=-1;
            int startMonth = 0;
            if (semester==1) {
                LocalDate date = LocalDate.of(year, 3, 1);
                DayOfWeek dayOfWeek = date.getDayOfWeek();
                dayOfWeekNum = dayOfWeek.getValue();
                startMonth=3;
            } else if(semester ==2){
                LocalDate date = LocalDate.of(year, 9, 1);
                DayOfWeek dayOfWeek = date.getDayOfWeek();
                dayOfWeekNum = dayOfWeek.getValue();
                startMonth=9;
            }

            for (int m=startMonth; m<startMonth+4; m++){ //3월 ~ 6월
                int week = 1;
                int ld = switch (m) {
                    case 3, 5,10,12 -> 31;
                    case 4, 6,9,11 -> 30;
                    default -> throw new IllegalStateException("Unexpected value: " + m);
                };
                for (int d=1; d<=ld; d++){
                    if (dayOfWeekNum==7) dayOfWeekNum=0;
                    if (dayOfWeekNum-1 == day){
                        boolean holiday= day == 6 || day == 7;
                        String ymd = String.format("%04d",year)
                                + String.format("%02d",m)
                                + String.format("%02d",d);
                        try{
                            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
                            Date date = dayFormat.parse(ymd);
                            calendar = Calendar.builder()
                                    .date(date)
                                    .year(year)
                                    .month(m)
                                    .day(d)
                                    .week(week++)
                                    .dayOfWeek(day)
                                    .Holiday(holiday)
                                    .build();
                            calendarRepository.save(calendar);
                        } catch(Exception e){
                            System.out.println(e.getMessage());
                        }
                        for (Job e : everyTimeJobs){
                            TimeTable table = TimeTable.builder()
                                    .calendar(calendar)
                                    .job(e)
                                    .user(null)
                                    .build();
                            timeTableRepository.save(table);
                        }

                    }

                    dayOfWeekNum+=1;
                }
            }
        }
        System.out.println("에브리타임 입력 완료");
        return everyTimeJobs;
    }
}
