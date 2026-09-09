package com.apiece.coupon.application;

import com.apiece.coupon.api.dto.CreateCouponRequest;
import com.apiece.coupon.domain.*;
import com.apiece.coupon.infrastructure.messaging.IssuanceRequested;
import com.apiece.coupon.support.CouponNotFoundException;
import com.apiece.coupon.support.NotStartedException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponIssuer couponIssuer;
    private final ApplicationEventPublisher eventPublisher;

    public CouponService(CouponRepository couponRepository, CouponIssuer couponIssuer, ApplicationEventPublisher eventPublisher) {
        this.couponRepository = couponRepository;
        this.couponIssuer = couponIssuer;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Coupon createCoupon(CreateCouponRequest request){
        Coupon coupon = couponRepository.save(
                new Coupon(
                        request.getName(),
                        request.getTotalQuantity(),
                        request.getValidityDays(),
                        request.getStartsAt()
                )
        );

        couponIssuer.initStock(Objects.requireNonNull(coupon.getId()),coupon.getTotalQuantity());
        return coupon;
    }

    @Transactional
    public Issuance issue(Long couponId, Long userId){
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(CouponNotFoundException::new);

        LocalDateTime now = LocalDateTime.now();
        if (!coupon.isBookingOpen(now)) {
            throw new NotStartedException();
        }

        couponIssuer.tryIssue(couponId, userId);

        LocalDateTime expiresAt = now.plusDays(Long.valueOf(coupon.getValidityDays()));
        eventPublisher.publishEvent(
                new IssuanceRequested(couponId,userId,now,expiresAt)
        );
        return new Issuance(userId, couponId, now, expiresAt);
    }
}
