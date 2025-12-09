package com.curciutbreaker.demo.executor;

import com.curciutbreaker.demo.helper.CallableDecorator;
import com.curciutbreaker.demo.helper.RunnableDecorator;
import com.curciutbreaker.demo.helper.SupplierDecorator;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.vavr.control.Try;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

@Component
public class ResilientExecutor {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;
    private final RateLimiterRegistry rateLimiterRegistry;
    private final BulkheadRegistry bulkheadRegistry;

    public ResilientExecutor(CircuitBreakerRegistry circuitBreakerRegistry,
                             RetryRegistry retryRegistry,
                             RateLimiterRegistry rateLimiterRegistry,
                             BulkheadRegistry bulkheadRegistry) {

        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.retryRegistry = retryRegistry;
        this.rateLimiterRegistry = rateLimiterRegistry;
        this.bulkheadRegistry = bulkheadRegistry;
    }

    // -----------------------------
    // Supplier
    // -----------------------------
    public <T> T execute(String cbName,
                         Supplier<T> supplier,
                         Supplier<T> fallback) {

        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker(cbName);
        Retry retry = retryRegistry.retry(cbName);
        RateLimiter rl = rateLimiterRegistry.rateLimiter(cbName);
        Bulkhead bulkhead = bulkheadRegistry.bulkhead(cbName);

        Supplier<T> decorated =
                SupplierDecorator.decorate(supplier, cb, retry, rl, bulkhead);

        return Try.ofSupplier(decorated)
                .recover(ex -> fallback.get())
                .get();
    }

    // -----------------------------
    // Callable
    // -----------------------------
    public <T> T executeCallable(String cbName,
                                 Callable<T> callable,
                                 Supplier<T> fallback) {

        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker(cbName);
        Retry retry = retryRegistry.retry(cbName);
        RateLimiter rl = rateLimiterRegistry.rateLimiter(cbName);
        Bulkhead bulkhead = bulkheadRegistry.bulkhead(cbName);

        Callable<T> decorated =
                CallableDecorator.decorate(callable, cb, retry, rl, bulkhead);

        return Try.ofCallable(decorated)
                .recover(ex -> fallback.get())
                .get();
    }

    // -----------------------------
    // Void
    // -----------------------------
    public void executeVoid(String cbName,
                            Runnable runnable,
                            Runnable fallback) {

        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker(cbName);
        Retry retry = retryRegistry.retry(cbName);
        RateLimiter rl = rateLimiterRegistry.rateLimiter(cbName);
        Bulkhead bulkhead = bulkheadRegistry.bulkhead(cbName);

        Runnable decorated =
                RunnableDecorator.decorate(runnable, cb, retry, rl, bulkhead);

        Try.run(decorated::run)
                .onFailure(ex -> fallback.run());
    }
}