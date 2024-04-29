//package com.example.areyoup;
//
//import static org.hamcrest.Matchers.*;
//import com.example.areyoup.member.repository.MemberRepository;
//import com.example.areyoup.timetable.service.TimeTableService;
//import jakarta.annotation.PostConstruct;
//import org.awaitility.Awaitility;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.test.context.support.WithUserDetails;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.Duration;
//import java.util.HashMap;
//import java.util.List;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//
//@SpringBootTest
//@TestPropertySource(properties = {
//        "schedules.cron.reward.publish=0/10 * * * * ?"
//})
//@ActiveProfiles("test")
//@Transactional(readOnly = true)
//class RunScheduledTest {
//
//    @Autowired
//    TimeTableService timeTableService;
//    @Autowired
//    MemberRepository memberRepository;
//
//    @PostConstruct
//    @Transactional
//    public void 로그인(){
//        given()
//            .contentType(ContentType.JSON)
//            .body("{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}")
//            .when()
//            .post("/users/login")
//            .then()
//            // Then: Verify the response
//            .statusCode(200) // Assuming 200 is the success status code for login
//            .body("token", not(emptyOrNullString())); // Assuming the response contains a token
//    }
//    }
//
//    @Test
//    @WithUserDetails(value = "kmg")
//    @Transactional
//    public void 자동스케줄링의_여부_확인(){
//        List seperatedJobs = timeTableService.getTable("2024.04.21", "2024.04.27").get("SeperatedJob");
//        assertThat(seperatedJobs.size()).isEqualTo(5);
//        Awaitility.await()
//                .atMost(Duration.ofSeconds(11))
//                .untilAsserted(()->{
//                        List later = timeTableService.getTable("2024.04.21", "2024.04.27").get("SeperatedJob");
//                        assertThat(later.size()).isGreaterThan(5);
//
//                });
//    }
//
//}
