package com.example.areyoup.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode{
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    AUTHENTICATION_FAILED(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호가 옳지 않습니다."),
    MEMBER_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 존재하는 회원입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
