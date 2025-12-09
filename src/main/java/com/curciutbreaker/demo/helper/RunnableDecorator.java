package com.curciutbreaker.demo.helper;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.retry.Retry;


public class RunnableDecorator {

    public static Runnable decorate(
            Runnable runnable,
            CircuitBreaker cb,
            Retry retry,
            RateLimiter rl,
            Bulkhead bulk
    ) {
        return Bulkhead
                .decorateRunnable(bulk,
                        RateLimiter.decorateRunnable(rl,
                                Retry.decorateRunnable(retry,
                                        CircuitBreaker.decorateRunnable(cb, runnable))));
    }
}