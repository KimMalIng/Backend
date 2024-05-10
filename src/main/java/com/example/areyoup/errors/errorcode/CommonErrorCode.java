package com.example.areyoup.errors.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode{

    IO_EXCEPTION(HttpStatus.BAD_REQUEST, ""),
    PARSE_EXCEPTION(HttpStatus.BAD_REQUEST, ""),
    INTERRUPT_EXCEPTION(HttpStatus.BAD_REQUEST, ""),
    NON_UNIQUE_RESULT_EXCEPTION(HttpStatus.BAD_REQUEST, "");

    private final HttpStatus httpStatus;
    private static String message;

    CommonErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        DateErrorCode.setMessage(message);
    }

    public static void setMessage(String message) {
        CommonErrorCode.message = message;
    }

    public String getMessage() {
        return message;
    }
}
