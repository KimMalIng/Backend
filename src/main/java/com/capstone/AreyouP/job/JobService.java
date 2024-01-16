package com.capstone.AreyouP.job;

import com.capstone.AreyouP.timetable.dto.EveryTimeDto;
import com.capstone.AreyouP.timetable.dto.TimeLineDto;
import com.capstone.AreyouP.job.dto.JobDto;
import com.capstone.AreyouP.calendar.Calendar;
import com.capstone.AreyouP.timetable.TimeTable;
import com.capstone.AreyouP.member.domain.Member;
import com.capstone.AreyouP.calendar.CalendarRepository;
import com.capstone.AreyouP.timetable.TimeTableRepository;
import com.capstone.AreyouP.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
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
    private final MemberRepository memberRepository;

    public String saveEveryTime(List<EveryTimeDto> everyTimeDtos, Long user_id) throws ParseException {

        //캘린더, 유저, job 정보를 timetable repository에 저장
        //year, semester에 따라 3.2 - 6.14 9.1 - 12.14 판단 가능, day에 따라 해당 날짜 중 같은 요일에 저장될 수 있도록
        //user 정보는 아직 ,, calendar에 잘 들어가냐만 판단해보잣!

        Member member = memberRepository.findById(user_id)
                .orElseThrow(()-> new EntityNotFoundException("사용자를 찾을 수 없습니다"));

        int year=0, semester = 0;
        String day;
        Job everyTimeJob;
        EveryTimeDto everyTimeDto = everyTimeDtos.get(everyTimeDtos.size()-1);
        year = everyTimeDto.getYear();
        semester = Integer.parseInt(everyTimeDto.getSemester());
        List<TimeLineDto> timeLineDtoList = everyTimeDto.getTimeline();
        List<Job> everyTimeJobs = new ArrayList<>();


        for (TimeLineDto timeLineDto : timeLineDtoList) {
            everyTimeJobs.clear();
            List<JobDto> subject = timeLineDto.getSubject();
            for (JobDto everyTime : subject) {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                Date date1 = format.parse(everyTime.getStartTime());
                Date date2 = format.parse(everyTime.getEndTime());

                // 시간 차이 계산
                long difference = date2.getTime() - date1.getTime();
                int estimatedTimeInMinutes = (int) (difference / (60 * 1000));

                int hours = estimatedTimeInMinutes / 60;
                int minutes = estimatedTimeInMinutes % 60;

                String estimated_Time =  String.format("%02d:%02d", hours, minutes);

                everyTimeJob = Job.builder()
                        .startTime(everyTime.getStartTime())
                        .endTime(everyTime.getEndTime())
                        .name(everyTime.getName())
                        .estimated_time(estimated_Time)
                        .label(0)
                        .build();
                everyTimeJobs.add(everyTimeJob);
                jobRepository.save(everyTimeJob);
            }

            day = timeLineDto.getDay();
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
                    Calendar calendar = new Calendar();
                    if (dayOfWeekNum==7) dayOfWeekNum=0;
                    int intDay = Integer.parseInt(day) == 6? 1 : Integer.parseInt(day)+1;
                    if (dayOfWeekNum == intDay){
                        // 0 일 1 월 2 화 3 수 4 목 5 금 6 토 dayOfWeekNum
                        boolean holiday= dayOfWeekNum==6 || dayOfWeekNum == 0;
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
                                    .dayOfWeek(String.valueOf(intDay))
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
                                    .member(member)
                                    .build();
                            timeTableRepository.save(table);
                        }

                    }
                    dayOfWeekNum+=1;
                }
            }
        }
        return "School TimeTable Save";
    }

    public String saveJob(JobDto jobDto, Long user_id) {
        Job job = Job.builder()
                .name(jobDto.getName())
                .label(jobDto.getLabel())
                .deadline(jobDto.getDeadline())
                .estimated_time(jobDto.getEstimated_time())
                .build();
        jobRepository.save(job);
        TimeTable timeTable = TimeTable.builder()
                .job(job)
                .member(memberRepository.findById(user_id)
                        .orElseThrow(() -> new ExpressionException("사용자를 찾을 수 없습니다.")))
                .calendar(null)
                .build();
        timeTableRepository.save(timeTable);
        return "Job Save";
    }

    public List<JobDto> getJob(Long userId) throws ParseException {
        List<Job> jobs = timeTableRepository.findJobByUserId(userId);
        if (jobs.isEmpty()) throw new EntityNotFoundException("일정을 찾을 수 없습니다.");
        else {
            List<JobDto> jobDtos = new ArrayList<>();
            for (Job j : jobs){
                JobDto jobDto = j.toJobDto(j);
                jobDtos.add(jobDto);
            }
            return jobDtos;
        }
    }

    @Transactional
    public Job modifyJob(JobDto jobDto) {

        Job job = Job.builder()
                .label(jobDto.getLabel())
                .name(jobDto.getName())
                .deadline(jobDto.getDeadline())
                .startTime(jobDto.getStartTime())
                .endTime(jobDto.getEndTime())
                .isPrivate(jobDto.isPrivate())
                .isComplete(jobDto.isComplete())
                .build();

        jobRepository.save(job);
        return job;
    }

    public void deleteJob(Long jobId) {
        jobRepository.deleteById(jobId);
    }

    @Transactional
    public Boolean completeJob(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(()-> new EntityNotFoundException("일정을 찾을 수 없습니다."));
        boolean isComplete = job.isComplete();
        isComplete = !isComplete;
        Job j = Job.builder()
                .isComplete(isComplete)
                .build();
        jobRepository.save(j);
        return isComplete;
    }
}
