package com.apiece.coupon.api.dto;

import com.apiece.coupon.domain.Coupon;

import java.time.LocalDateTime;
import java.util.Objects;

public class CouponResponse {

    private final long id;
    private final String name;
    private final int totalQuantity;
    private final int validityDays;
    private final int issuedQuantity;
    private final LocalDateTime startsAt;
    private final LocalDateTime createdAt;


    public CouponResponse(long id, String name, int totalQuantity, int validityDays, int issuedQuantity, LocalDateTime startsAt, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.totalQuantity = totalQuantity;
        this.validityDays = validityDays;
        this.issuedQuantity = issuedQuantity;
        this.startsAt = startsAt;
        this.createdAt = createdAt;
    }

    public static CouponResponse from(Coupon coupon){
        return new CouponResponse(
                Objects.requireNonNull(coupon.getId()),
                coupon.getName(),
                coupon.getTotalQuantity(),
                coupon.getValidityDays(),
                coupon.getIssuedQuantity(),
                coupon.getStartsAt(),
                coupon.getCreatedAt()
        );
    }

}
