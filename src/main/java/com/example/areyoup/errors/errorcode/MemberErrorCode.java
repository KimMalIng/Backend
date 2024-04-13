package com.example.areyoup.errors.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode{
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    AUTHENTICATION_FAILED(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호가 옳지 않습니다."),
    MEMBER_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 존재하는 회원입니다."),
    IMAGE_SAVE_ERROR(HttpStatus.BAD_REQUEST, "이미지 저장 중 오류 발생"),

    REFRESH_TOKEN_ERROR(HttpStatus.NOT_FOUND, "해당 RefreshToken에 맞는 회원이 존재하지 않습니다."),
    OAUTH_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "소셜 로그인된 계정이 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
