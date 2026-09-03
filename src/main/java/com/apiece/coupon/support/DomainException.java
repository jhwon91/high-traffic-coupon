package com.apiece.coupon.support;

import org.springframework.http.HttpStatus;

public abstract class DomainException extends RuntimeException{
    private final String code;
    private final HttpStatus httpStatus;

    DomainException(String code, HttpStatus httpStatus, String message){
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }


}
