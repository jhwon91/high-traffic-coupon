package com.apiece.coupon.api;

import com.apiece.coupon.api.dto.CouponResponse;
import com.apiece.coupon.api.dto.CreateCouponRequest;
import com.apiece.coupon.api.dto.IssuanceResponse;
import com.apiece.coupon.application.CouponService;
import com.apiece.coupon.domain.Coupon;
import com.apiece.coupon.domain.Issuance;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CouponController {


    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping
    public ResponseEntity<CouponResponse> create(@RequestBody CreateCouponRequest request) {
        Coupon coupon = couponService.createCoupon(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(CouponResponse.from(coupon));
    }

    @PostMapping("/{couponId}/issue")
    public IssuanceResponse issue(@PathVariable Long couponId, @RequestHeader("X-User-Id") Long userId){
        Issuance issue = couponService.issue(couponId, userId);
        return IssuanceResponse.from(issue);
    }
}
