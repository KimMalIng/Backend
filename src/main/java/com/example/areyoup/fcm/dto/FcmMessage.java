package com.example.areyoup.fcm.dto;

import lombok.*;

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
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RequestDto{
        private String fcmToken;
        private String title;
        private String message;
    }

}
