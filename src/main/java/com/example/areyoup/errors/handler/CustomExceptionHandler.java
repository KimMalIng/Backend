package com.example.areyoup.errors.handler;

import com.example.areyoup.errors.entity.ErrorResponseEntity;
import com.example.areyoup.errors.errorcode.CommonErrorCode;
import com.example.areyoup.errors.errorcode.DateErrorCode;
import com.example.areyoup.errors.errorcode.MemberErrorCode;
import com.example.areyoup.errors.exception.CustomException;
import com.example.areyoup.errors.exception.JobException;
import com.example.areyoup.errors.exception.MemberException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.attoparser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.lang.reflect.MalformedParametersException;
import java.time.DateTimeException;
import java.time.format.DateTimeParseException;

@ControllerAdvice
//모든 Controller 즉, 전역에서 발생할 수 있는 예외를 잡아 처리
//todo Handler 작업
public class CustomExceptionHandler {

    @ExceptionHandler(IOException.class)
    private ResponseEntity<ErrorResponseEntity> handleIOException(CustomException e) {
        // IOException에 대한 처리
        CommonErrorCode.setMessage(e.getMessage());
        return ErrorResponseEntity.toResponseEntity(CommonErrorCode.IO_EXCEPTION);
    }

    @ExceptionHandler(ParseException.class)
    private ResponseEntity<ErrorResponseEntity> handleParseException(ParseException e) {
        // ParseException에 대한 처리
        CommonErrorCode.setMessage(e.getMessage());
        return ErrorResponseEntity.toResponseEntity(CommonErrorCode.PARSE_EXCEPTION);
    }

    @ExceptionHandler(InterruptedException.class)
    private ResponseEntity<ErrorResponseEntity> handleInterruptedException(InterruptedException e) {
        // InterruptedException에 대한 처리
        CommonErrorCode.setMessage(e.getMessage());
        return ErrorResponseEntity.toResponseEntity(CommonErrorCode.INTERRUPT_EXCEPTION);
    }

    @ExceptionHandler(JobException.class)
    //발생한 CustomException 예외를 잡아서 하나의 메소드에서 공통 처리
    protected ResponseEntity<ErrorResponseEntity> handleJobException(JobException e){
        return ErrorResponseEntity.toResponseEntity(e.getErrorCode());
    }
    // 즉, 모든 컨트롤러에서 발생하는 CustomExceptino을 catch한다.

    @ExceptionHandler(MemberException.class)
    //발생한 CustomException 예외를 잡아서 하나의 메소드에서 공통 처리
    protected ResponseEntity<ErrorResponseEntity> handleMemberException(MemberException e){
        return ErrorResponseEntity.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler({DateTimeParseException.class, DateTimeException.class})
    protected ResponseEntity<ErrorResponseEntity> handleDateTimeException(DateTimeParseException e){
        DateErrorCode.setMessage(e.getMessage());
        return ErrorResponseEntity.toResponseEntity(DateErrorCode.DATE_ERROR_CODE);
    }

    @ExceptionHandler({SecurityException.class, MalformedJwtException.class, ExpiredJwtException.class, UnsupportedJwtException.class, IllegalArgumentException.class})
    protected ResponseEntity<ErrorResponseEntity> handleJWTException(MemberException e){
        return ErrorResponseEntity.toResponseEntity(e.getErrorCode());
    }

}
