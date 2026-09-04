package com.apiece.coupon.api.dto;

import com.apiece.coupon.domain.Issuance;

import java.time.LocalDateTime;
import java.util.Objects;

public class IssuanceResponse {


    private final long id;
    private final long userId;
    private final long couponId;
    private final LocalDateTime issuedAt;
    private final LocalDateTime expiresAt;
    private final LocalDateTime usedAt;

    public IssuanceResponse(long id, long userId, long couponId, LocalDateTime issuedAt, LocalDateTime expiresAt, LocalDateTime usedAt) {
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.usedAt = usedAt;
    }

    public static IssuanceResponse from (Issuance issuance){
        return new IssuanceResponse(
                Objects.requireNonNull(issuance.getId()),
                issuance.getUserId(),
                issuance.getCouponId(),
                issuance.getIssuedAt(),
                issuance.getExpiresAt(),
                issuance.getUsedAt()
        );
    }

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public long getCouponId() {
        return couponId;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

}
