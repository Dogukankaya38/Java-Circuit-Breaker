package com.curciutbreaker.demo.helper;


import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.retry.Retry;

import java.util.concurrent.Callable;

public class CallableDecorator {

    public static <T> Callable<T> decorate(
            Callable<T> callable,
            CircuitBreaker cb,
            Retry retry,
            RateLimiter rl,
            Bulkhead bulk
    ) {
        return Bulkhead
                .decorateCallable(bulk,
                        RateLimiter.decorateCallable(rl,
                                Retry.decorateCallable(retry,
                                        CircuitBreaker.decorateCallable(cb, callable))));
    }
}