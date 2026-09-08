package com.apiece.coupon.application;

import com.apiece.coupon.support.AlreadyIssuedException;
import com.apiece.coupon.support.SoldOutException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CouponIssuer {
    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> script = RedisScript.of(
            new ClassPathResource("lua/issue.lua"),
            Long.class
    );

    public CouponIssuer(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void tryIssue(Long couponId, Long userId) {
        Long raw = redisTemplate.execute(
                script,
                List.of(stockKey(couponId), usersKey(couponId)),
                userId.toString()
        );

        if (raw == null) {
            throw new IllegalArgumentException("Lua 스크립트 결과가 null");
        }

        if (raw == 1) {
            return;
        }

        if (raw == 0) {
            throw new SoldOutException();
        }

        if (raw == -1) {
            throw new AlreadyIssuedException();
        }

        throw new IllegalStateException("예상치 못한 Lua 결과: " + raw);
    }

    public void initStock(Long couponId, int totalQuantity) {
        redisTemplate.opsForValue().set(stockKey(couponId),String.valueOf(totalQuantity));
    }

    private String stockKey(Long couponId) {
        return "coupon:" + couponId + ":stock";
    }
    private String usersKey(Long couponId) {
        return "coupon:" + couponId + ":users";
    }
}
