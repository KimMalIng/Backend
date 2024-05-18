package com.example.areyoup.fcm.controller;

import com.example.areyoup.fcm.dto.FcmMessage;
import com.example.areyoup.fcm.service.fcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fcm")
public class fcmController {

    private final fcmService fcmService;

    @Transactional
    @PutMapping("/update/fcmToken")
    public ResponseEntity<?> updateToken(@RequestBody FcmMessage.UpdateDto updateDto){
        return ResponseEntity.ok()
                .body(fcmService.updateToken(updateDto));
    }
}
