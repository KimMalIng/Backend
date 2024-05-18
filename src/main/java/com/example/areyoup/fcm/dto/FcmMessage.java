package com.example.areyoup.fcm.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class FcmMessage {

    @Getter
    @Setter
    public static class UpdateDto{
        private String fcmToken;
    }

    @Builder
    @Getter
    @Setter
    public static class RequestDto{
        private String fcmToken;
        private String title;
        private String message;
    }

}
