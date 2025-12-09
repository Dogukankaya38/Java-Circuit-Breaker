package com.curciutbreaker.demo.logger;

import io.github.resilience4j.bulkhead.*;
import io.github.resilience4j.circuitbreaker.*;
import io.github.resilience4j.ratelimiter.*;
import io.github.resilience4j.retry.*;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Resilience4jEventLogger {

    private final RetryRegistry retryRegistry;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RateLimiterRegistry rateLimiterRegistry;
    private final BulkheadRegistry bulkheadRegistry;

    @PostConstruct
    public void init() {
        log.info("Resilience4jEventLogger initialized… attaching all event listeners");

        retryRegistry.getAllRetries().forEach(this::attachRetryLogger);
        retryRegistry.getEventPublisher().onEntryAdded(e -> attachRetryLogger(e.getAddedEntry()));

        circuitBreakerRegistry.getAllCircuitBreakers().forEach(this::attachCircuitBreakerLogger);
        circuitBreakerRegistry.getEventPublisher().onEntryAdded(e -> attachCircuitBreakerLogger(e.getAddedEntry()));

        rateLimiterRegistry.getAllRateLimiters().forEach(this::attachRateLimiterLogger);
        rateLimiterRegistry.getEventPublisher().onEntryAdded(e -> attachRateLimiterLogger(e.getAddedEntry()));

        bulkheadRegistry.getAllBulkheads().forEach(this::attachBulkheadLogger);
        bulkheadRegistry.getEventPublisher().onEntryAdded(e -> attachBulkheadLogger(e.getAddedEntry()));
    }


    private void attachRetryLogger(Retry retry) {
        retry.getEventPublisher()
                .onRetry(ev -> log.warn(
                        "[Retry:{}] RETRY attempt={} | exception={}",
                        retry.getName(), ev.getNumberOfRetryAttempts(), ev.getLastThrowable()))
                .onSuccess(ev -> log.info(
                        "[Retry:{}] SUCCESS after={} retries",
                        retry.getName(), ev.getNumberOfRetryAttempts()))
                .onError(ev -> log.error(
                        "[Retry:{}] ERROR after retries={} | exception={}",
                        retry.getName(), ev.getNumberOfRetryAttempts(), ev.getLastThrowable()));
    }

    private void attachCircuitBreakerLogger(CircuitBreaker cb) {
        cb.getEventPublisher()
                .onStateTransition(ev -> log.warn(
                        "[CB:{}] STATE CHANGE {} -> {}",
                        cb.getName(),
                        ev.getStateTransition().getFromState(),
                        ev.getStateTransition().getToState()))
                .onError(ev -> log.error(
                        "[CB:{}] ERROR | duration={}ms | exception={}",
                        cb.getName(), ev.getElapsedDuration().getSeconds(), ev.getThrowable()))
                .onCallNotPermitted(ev -> log.warn(
                        "[CB:{}] CALL_NOT_PERMITTED", cb.getName()))
                .onSuccess(ev -> log.info(
                        "[CB:{}] SUCCESS | duration={}ms",
                        cb.getName(), ev.getElapsedDuration().getSeconds()));
    }

    private void attachRateLimiterLogger(RateLimiter rl) {
        rl.getEventPublisher()
                .onSuccess(ev -> log.info(
                        "[RateLimiter:{}] PERMIT OK", rl.getName()))
                .onFailure(ev -> log.warn(
                        "[RateLimiter:{}] PERMIT DENIED", rl.getName()));
    }

    private void attachBulkheadLogger(Bulkhead bh) {
        bh.getEventPublisher()
                .onCallPermitted(ev -> log.info(
                        "[Bulkhead:{}] CALL PERMITTED", bh.getName()))
                .onCallRejected(ev -> log.warn(
                        "[Bulkhead:{}] CALL REJECTED", bh.getName()))
                .onCallFinished(ev -> log.debug(
                        "[Bulkhead:{}] CALL FINISHED", bh.getName()));
    }
}
