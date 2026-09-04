package com.apiece.coupon.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssuanceRepository extends JpaRepository<Issuance, Long> {

    boolean existsByUserIdAndCouponId(Long userId, Long couponId);
    List<Issuance> findByUserIdOrderByIssuedAtDesc(Long userId);
}
