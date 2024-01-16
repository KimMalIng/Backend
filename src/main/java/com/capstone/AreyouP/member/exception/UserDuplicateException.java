package com.capstone.AreyouP.member.exception;

public class UserDuplicateException extends RuntimeException{
    public UserDuplicateException(String message) {
        super(message);
    }
}