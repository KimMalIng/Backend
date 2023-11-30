package com.capstone.AreyouP.Exception;

public class UserDuplicateException extends RuntimeException{
    public UserDuplicateException(String message) {
        super(message);
    }
}