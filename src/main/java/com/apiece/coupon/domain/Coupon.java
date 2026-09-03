package com.apiece.coupon.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupon")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length =80)
    private String name;

    @Column(nullable = false)
    private int totalQuantity;

    @Column(nullable = false)
    private int validityDays;

    private LocalDateTime startsAt;

    @Column(nullable = false)
    private int issuedQuantity;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


    protected Coupon(){}

    public Coupon(String name, int totalQuantity, int validityDays, LocalDateTime startsAt) {
        this(null, name, totalQuantity, validityDays, startsAt, 0, LocalDateTime.now());
    }

    public Coupon(Long id, String name, int totalQuantity, int validityDays, LocalDateTime startsAt, int issuedQuantity, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.totalQuantity = totalQuantity;
        this.validityDays = validityDays;
        this.startsAt = startsAt;
        this.issuedQuantity = issuedQuantity;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
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

    public int getIssuedQuantity() {
        return issuedQuantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Boolean isBookingOpen(LocalDateTime now) {
        return startsAt == null || now.isBefore(startsAt);
    }

    public Boolean isSoldOut() {
        return issuedQuantity >= totalQuantity;
    }

    public void incrementIssuedQuantity() {
        this.issuedQuantity++;
    }
}
