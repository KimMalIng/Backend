package com.example.areyoup.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_TOKEN(HttpStatus.NOT_FOUND, "Token이 유효하지 않습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    AUTHENTICATION_FAILED(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호가 옳지 않습니다."),
    MEMBER_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 존재하는 회원입니다."),

    JOB_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 일정입니다."),
    JOB_AUTHOR_MISMATCH(HttpStatus.FORBIDDEN, "해당 일정에 접근 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String msg;
}
