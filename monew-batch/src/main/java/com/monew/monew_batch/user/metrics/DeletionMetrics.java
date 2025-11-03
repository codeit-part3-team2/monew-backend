package com.monew.monew_batch.user.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 사용자 삭제 배치 작업에 대한 커스텀 메트릭
 * Spring Actuator를 통해 Prometheus/Grafana로 노출됩니다.
 */
@Slf4j
@Component
public class DeletionMetrics {

    private final Counter deletedUserCounter;

    public DeletionMetrics(MeterRegistry meterRegistry) {
        this.deletedUserCounter = Counter.builder("user.deletion.count")
                .description("Total number of permanently deleted users")
                .tag("type", "batch")
                .register(meterRegistry);

        log.info("DeletionMetrics initialized");
    }

    /**
     * 삭제된 사용자 수 증가
     */
    public void incrementDeletedUserCount() {
        deletedUserCounter.increment();
        log.debug("Deleted user count incremented: current={}", deletedUserCounter.count());
    }

    /**
     * 삭제된 사용자 수를 지정된 값만큼 증가
     */
    public void incrementDeletedUserCount(long count) {
        deletedUserCounter.increment(count);
        log.debug("Deleted user count incremented by {}: current={}", count, deletedUserCounter.count());
    }

    /**
     * 현재까지 삭제된 사용자 수 조회
     */
    public double getDeletedUserCount() {
        return deletedUserCounter.count();
    }
}
