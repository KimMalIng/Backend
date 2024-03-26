package com.example.areyoup.errors.exception;

import com.example.areyoup.errors.errorcode.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JobException extends RuntimeException{
    private final ErrorCode ErrorCode;
}
