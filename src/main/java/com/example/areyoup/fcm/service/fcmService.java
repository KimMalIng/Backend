package com.example.areyoup.fcm.service;

import com.example.areyoup.fcm.domain.fcmEntity;
import com.example.areyoup.fcm.dto.FcmMessage;
import com.example.areyoup.fcm.repository.fcmRepository;
import com.example.areyoup.job.domain.Job;
import com.example.areyoup.job.repository.JobRepository;
import com.example.areyoup.member.domain.Member;
import com.example.areyoup.member.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class fcmService {

    private final String API_URL = "https://data.kimmaling.com/fcm/send";
    private final ObjectMapper objectMapper;
    private final MemberService memberService;
    private final HttpServletRequest request;
    private final JobRepository jobRepository;
    private final fcmRepository fcmRepository;

    private final String START_TITLE = " 일정을 시작할 시간 이에요!";
    private final String START_BODY = " 만큼 집중해 보아요.";

    private final String END_TITLE = " 일정을 끝마칠 시간 이에요!";
    private final String END_BODY = "현재 진행한 일정의 완료도를 입력 하세요!";


    @Scheduled(cron = "0 */1 * * * *")
    public void notificationJob() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        List<Job> startJobs = jobRepository.findAllByDayAndStartTimeEquals(now.toLocalDate(), String.valueOf(now.toLocalTime()).substring(0,5));
        List<Job> endJobs = jobRepository.findAllByDayAndEndTimeEquals(now.toLocalDate(), String.valueOf(now.toLocalTime()).substring(0,5));
        log.info("Alarm On");
        for (Job startJob : startJobs){
            log.info("Alarm for startJob");
            sendMessageTo(startJob.getMember().getFcm().getFcmToken(), startJob.getName()+START_TITLE, startJob.getEstimatedTime()+START_BODY);
        }
        for (Job endJob : endJobs){
            log.info("Alarm for endJob");
            sendMessageTo(endJob.getMember().getFcm().getFcmToken(), endJob.getName()+END_TITLE, END_BODY);
        }
    }

    public String sendMessageTo(String fcmToken, String title, String body) throws IOException {
        log.info("fcmToken : " + fcmToken + ", title :"+ title + ", body :" + body);
        String message = makeMessage(fcmToken, title, body);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message,
                MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                .build();

        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    private String makeMessage(String fcmToken, String title, String message) throws JsonProcessingException {
        FcmMessage.RequestDto fcmMessage = FcmMessage.RequestDto.builder()
                .fcmToken(fcmToken)
                .title(title)
                .message(message)
                .build();

        return objectMapper.writeValueAsString(fcmMessage);
    }

    public String updateToken(FcmMessage.UpdateDto updateDto) {
        Member m = memberService.findMember(request);
        if (m.getFcm()==null){
            fcmEntity fcm = fcmEntity.builder()
                    .fcmToken(updateDto.getFcmToken()).build();
            fcmRepository.save(fcm);
            m.toUpdateFcm(fcm);
        } else{
            m.getFcm().toUpdateFcmToken(updateDto.getFcmToken());
        }
        return updateDto.getFcmToken();
    }
}