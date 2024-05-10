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
    OAUTH_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "로그인 된 카카오 계정이 없습니다."),

    INVALID_TOKEN(HttpStatus.NOT_FOUND, "Token이 유효하지 않습니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "토큰이 만료되었습니다"),
    UNSUPPORTED_JWT_EXCEPTION(HttpStatus.BAD_REQUEST, "지원되지 않는 토큰입니다"),
    ILLEGAL_ARGUMENT_EXCEPTION(HttpStatus.BAD_REQUEST, "토큰이 올바른 형식이 아니거나 claim이 비어 있습니다.");


    private final HttpStatus httpStatus;
    private final String message;
}
