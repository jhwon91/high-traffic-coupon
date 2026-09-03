package com.apiece.coupon.api.dto;

import java.time.LocalDateTime;

public class CreateCouponRequest {

    private final String name;

    private final int totalQuantity;
    private final int validityDays;
    private final LocalDateTime startsAt;

    public CreateCouponRequest(String name, Integer totalQuantity, Integer validityDays, LocalDateTime startsAt) {
        this.name = name;
        this.totalQuantity = totalQuantity != null? totalQuantity : 5000;
        this.validityDays = validityDays != null? validityDays : 7;
        this.startsAt = startsAt;
    }

    public String getName() {
        return name;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public int getValidityDays() {
        return validityDays;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }
}
