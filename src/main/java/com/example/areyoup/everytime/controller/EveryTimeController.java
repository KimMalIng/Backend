package com.example.areyoup.everytime.controller;

import com.example.areyoup.everytime.service.EveryTimeService;
import com.example.areyoup.everytime.dto.EverytimeRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/everytime")
public class EveryTimeController {

    private final EveryTimeService everyTimeService;

    /*
    유저에 대한 everytime 시간표 저장
     */
    @PostMapping("/save/{member_id}")
    public ResponseEntity<String> saveEveryTime(@RequestBody List<EverytimeRequestDto.EverytimeDto> everytimeDtos,
                                                @PathVariable("member_id") Long member_id) throws ParseException {
        return ResponseEntity.ok()
                .body(everyTimeService.saveEveryTime(everytimeDtos, member_id));
    }
}
