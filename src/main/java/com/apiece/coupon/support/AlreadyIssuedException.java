package com.apiece.coupon.support;

import org.springframework.http.HttpStatus;

public class AlreadyIssuedException extends DomainException{

    public AlreadyIssuedException() {
        this("이미 사용된 쿠폰입니다");
    }

    public AlreadyIssuedException(String message) {
        super("ALREADY_USED", HttpStatus.CONFLICT, message);
    }
}
