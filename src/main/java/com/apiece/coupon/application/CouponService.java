package com.apiece.coupon.application;

import com.apiece.coupon.api.dto.CreateCouponRequest;
import com.apiece.coupon.domain.*;
import com.apiece.coupon.support.AlreadyIssuedException;
import com.apiece.coupon.support.CouponNotFoundException;
import com.apiece.coupon.support.NotStartedException;
import com.apiece.coupon.support.SoldOutException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final IssuanceRepository issuanceRepository;
    private final CouponIssuer couponIssuer;

    public CouponService(CouponRepository couponRepository, IssuanceRepository issuanceRepository, CouponIssuer couponIssuer) {
        this.couponRepository = couponRepository;
        this.issuanceRepository = issuanceRepository;
        this.couponIssuer = couponIssuer;
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
        // 비관적 락 적용
//        Coupon coupon = couponRepository.findByIdForUpdate(couponId)
//                .orElseThrow(CouponNotFoundException::new);

        LocalDateTime now = LocalDateTime.now();
        if (!coupon.isBookingOpen(now)) {
            throw new NotStartedException();
        }

        if (coupon.isSoldOut()) {
            throw new SoldOutException();
        }

        if (issuanceRepository.existsByUserIdAndCouponId(userId, couponId)) {
            throw new AlreadyIssuedException();
        }

        couponIssuer.tryIssue(couponId);
        couponRepository.incrementIssuedQuantity(couponId);

//        coupon.incrementIssuedQuantity();


        Issuance issuance = new Issuance(userId, couponId, now, now.plusDays(Long.valueOf(coupon.getValidityDays())));
        return issuanceRepository.save(issuance);
    }
}
