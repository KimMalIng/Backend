package com.example.areyoup.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum DateErrorCode implements ErrorCode{

    DATE_ERROR_CODE(HttpStatus.BAD_REQUEST, "");

    private final HttpStatus httpStatus;
    private static String message;

    DateErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        DateErrorCode.setMessage(message);
    }

    public static void setMessage(String message) {
        DateErrorCode.message = message;
    }

    public String getMessage() {
        return message;
    }
}