package com.apiece.coupon.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "issuance",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_issuance_user_coupon",
                        columnNames = {"user_id", "coupon_id"}
                )
        },
        indexes = {
                @Index(name = "idx_issuance_status", columnList = "status"),
                @Index(name = "idx_issuance_coupon", columnList = "coupon_id")
        }
)
public class Issuance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private long userId;

    @Column(nullable = false)
    private long couponId;

    @Column(nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private IssuanceStatus status;

    private LocalDateTime usedAt;

    protected Issuance(){}

    public Issuance(Long id, long userId, long couponId, LocalDateTime issuedAt, LocalDateTime expiresAt, IssuanceStatus status, LocalDateTime usedAt) {
        this.id = id;
        this.userId = userId;
        this.couponId = couponId;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.status = status;
        this.usedAt = usedAt;
    }

    public Issuance(long userId, long couponId, LocalDateTime issuedAt, LocalDateTime expiresAt) {
        this(null, userId, couponId, issuedAt, expiresAt, IssuanceStatus.ISSUED, null);
    }

    public Long getId() {
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

    public IssuanceStatus getStatus() {
        return status;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public boolean isExpired(LocalDateTime now) {
        return !now.isBefore(expiresAt);
    }

    public void markUsed(LocalDateTime now) {
        this.status = IssuanceStatus.USED;
        this.usedAt = now;
    }
}
