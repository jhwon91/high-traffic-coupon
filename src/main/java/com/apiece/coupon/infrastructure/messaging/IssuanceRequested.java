package com.apiece.coupon.infrastructure.messaging;

import java.time.LocalDateTime;

public class IssuanceRequested {

    private final Long couponId;
    private final Long userId;
    private final LocalDateTime issueAt;
    private final LocalDateTime expiresAt;

    public IssuanceRequested(Long couponId, Long userId, LocalDateTime issueAt, LocalDateTime expiresAt) {
        this.couponId = couponId;
        this.userId = userId;
        this.issueAt = issueAt;
        this.expiresAt = expiresAt;
    }

    public Long getCouponId() {
        return couponId;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDateTime getIssueAt() {
        return issueAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
}
