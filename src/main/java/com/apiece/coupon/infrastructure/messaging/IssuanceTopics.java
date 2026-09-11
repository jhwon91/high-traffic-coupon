package com.apiece.coupon.infrastructure.messaging;

public class IssuanceTopics {
    public static final String REQUESTED = "issuance.requested";
    public static final String REQUESTED_DLT = "issuance.requested.DLT";
    public static final String CONSUMER_GROUP = "issuance-worker";

    private IssuanceTopics() {
    }
}
