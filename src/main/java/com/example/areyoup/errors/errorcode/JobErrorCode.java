package com.example.areyoup.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum JobErrorCode implements ErrorCode{

    JOB_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 일정입니다."),
    JOB_AUTHOR_MISMATCH(HttpStatus.FORBIDDEN, "해당 일정에 접근 권한이 없습니다."),
    JOB_DUPLICATED(HttpStatus.BAD_REQUEST, "같은 이름의 일정이 존재합니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
