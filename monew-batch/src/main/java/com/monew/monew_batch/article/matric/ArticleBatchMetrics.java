package com.monew.monew_batch.article.matric;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 배치 통계 수집 메트릭
 * - Prometheus에서 batch.*.* 메트릭 이름으로 노출됨
 */
@Component
@RequiredArgsConstructor
public class ArticleBatchMetrics {

    private final MeterRegistry meterRegistry;

    public void recordArticles(int total, int newCount, int linkedCount) {
        meterRegistry.counter("batch.articles.total").increment(total);
        meterRegistry.counter("batch.articles.new").increment(newCount);
        meterRegistry.counter("batch.articles.linked").increment(linkedCount);
    }

    public void recordBackup(boolean success, int count) {
        if (success) {
            meterRegistry.counter("batch.backup.success").increment();
            meterRegistry.counter("batch.backup.count").increment(count);
        } else {
            meterRegistry.counter("batch.backup.fail").increment();
        }
    }

    public void recordCleanup(int deletedCount) {
        meterRegistry.counter("batch.cleanup.deleted").increment(deletedCount);
    }
}
