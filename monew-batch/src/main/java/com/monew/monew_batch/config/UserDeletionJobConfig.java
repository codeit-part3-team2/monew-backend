package com.monew.monew_batch.config;

import com.monew.monew_api.common.user.User;
import com.monew.monew_batch.metrics.UserDeletionMetrics;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * [요구사항] Soft delete 후 1일(24시간) 경과한 사용자를 영구 삭제
 * [프로토타입] 테스트 환경에서는 5분 후 삭제로 구현 (빠른 테스트를 위함)
 */
@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class UserDeletionJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final UserDeletionMetrics userDeletionMetrics;

    @Value("${batch.user-deletion.chunk-size:10}")
    private int chunkSize;

    // 프로토타입: 5분
    @Value("${batch.user-deletion.retention-minutes:5}")
    private int retentionMinutes;

    /**
     * 사용자 삭제 Job 정의
     */
    @Bean
    public Job userDeletionJob(EntityManager entityManager) {
        return new JobBuilder("userDeletionJob", jobRepository)
                .start(userDeletionStep(entityManager))
                .build();
    }

    /**
     * 사용자 삭제 Step 정의 (Chunk 기반 처리)
     */
    @Bean
    public Step userDeletionStep(EntityManager entityManager) {
        return new StepBuilder("userDeletionStep", jobRepository)
                .<User, User>chunk(chunkSize, transactionManager)
                .reader(userDeletionReader(null))
                .writer(userDeletionWriter(entityManager))
                .build();
    }

    /**
     * ItemReader: 삭제 대상 사용자 조회
     * [프로토타입] 5분 이전 = deletedAt < (현재 - 5분)
     */
    @Bean
    @StepScope
    public JpaPagingItemReader<User> userDeletionReader(
            @Value("#{jobParameters['runTime']}") Long runTime) {
        LocalDateTime cutoffDate = LocalDateTime.now().minus(retentionMinutes, ChronoUnit.MINUTES);

        log.info("UserDeletionReader 초기화 - cutoffDate: {}, retentionMinutes: {}", cutoffDate, retentionMinutes);

        return new JpaPagingItemReaderBuilder<User>()
                .name("userDeletionReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT u FROM User u WHERE u.deletedAt IS NOT NULL AND u.deletedAt < :cutoffDate")
                .parameterValues(Map.of("cutoffDate", cutoffDate))
                .pageSize(chunkSize)
                .build();
    }

    /**
     * ItemWriter: 사용자 영구 삭제
     * DB의 ON DELETE CASCADE로 연관 데이터 자동 삭제
     */
    @Bean
    public ItemWriter<User> userDeletionWriter(EntityManager entityManager) {
        return chunk -> {
            for (User user : chunk.getItems()) {
                User managedUser = entityManager.merge(user);
                entityManager.remove(managedUser);

                log.info("사용자 삭제: id={}, email={}", user.getId(), user.getEmail());
                userDeletionMetrics.incrementDeletedUserCount();
            }
            log.info("청크 완료: {}명 삭제", chunk.size());
        };
    }
}
