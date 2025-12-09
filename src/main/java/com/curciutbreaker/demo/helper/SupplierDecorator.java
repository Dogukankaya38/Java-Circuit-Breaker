package com.curciutbreaker.demo.helper;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.retry.Retry;

import java.util.function.Supplier;

public class SupplierDecorator {

    public static <T> Supplier<T> decorate(
            Supplier<T> supplier,
            CircuitBreaker cb,
            Retry retry,
            RateLimiter rl,
            Bulkhead bulk
    ) {
        return Bulkhead
                .decorateSupplier(bulk,
                        RateLimiter.decorateSupplier(rl,
                                Retry.decorateSupplier(retry,
                                        CircuitBreaker.decorateSupplier(cb, supplier))));
    }
}