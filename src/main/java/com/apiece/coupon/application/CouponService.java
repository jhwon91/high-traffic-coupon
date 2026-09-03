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

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final IssuanceRepository issuanceRepository;

    public CouponService(CouponRepository couponRepository, IssuanceRepository issuanceRepository) {
        this.couponRepository = couponRepository;
        this.issuanceRepository = issuanceRepository;
    }

    @Transactional
    public Coupon createCoupon(CreateCouponRequest request){
        Coupon coupon = new Coupon(
                request.getName(),
                request.getTotalQuantity(),
                request.getValidityDays(),
                request.getStartsAt()
        );

        return couponRepository.save(coupon);
    }

    @Transactional
    public Issuance issue(Long couponId, Long userId){
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(CouponNotFoundException::new);

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

        coupon.incrementIssuedQuantity();

        Issuance issuance = new Issuance(userId, couponId, now, now.plusDays(Long.valueOf(coupon.getValidityDays())));
        return issuanceRepository.save(issuance);
    }
}
