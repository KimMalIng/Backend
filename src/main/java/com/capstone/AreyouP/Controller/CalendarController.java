package com.capstone.AreyouP.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController {

//    @GetMapping("/{month}") //월 단위 화면이 나왔을 경우
//    public ResponseEntity<?> monthInfo(@PathVariable Integer month){
//
//    }
}
