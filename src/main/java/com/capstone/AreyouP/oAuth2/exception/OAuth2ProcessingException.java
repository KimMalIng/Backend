package com.capstone.AreyouP.oAuth2.exception;


import org.springframework.security.core.AuthenticationException;
public class OAuth2ProcessingException extends AuthenticationException {
    public OAuth2ProcessingException(String msg){
        super(msg);
    }
}
