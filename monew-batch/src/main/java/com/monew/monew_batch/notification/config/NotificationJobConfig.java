package com.monew.monew_batch.notification.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class NotificationJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JdbcTemplate jdbcTemplate;

    @Bean
    public Job deleteOldNotificationJob() {

        return new JobBuilder("deleteOldNotificationJob", jobRepository)
                .start(deleteOldNotificationStep())
                .build();
    }

    @Bean
    public Step deleteOldNotificationStep() {
        return new StepBuilder("deleteOldNotificationStep", jobRepository)
                .tasklet(deleteOldNotificationTasklet(), transactionManager)
                .build();
    }

    /**
     * 확인한지 일주일 경과한 알림 삭제
     */
    @Bean
    public Tasklet deleteOldNotificationTasklet() {
        return ((contribution, chunkContext) -> {
            log.info("[배치 시작] 확인한지 일주일이 경과한 알림 삭제 작업 시작");

            LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

            String sql = "DELETE FROM notifications WHERE confirmed = true AND updated_at < ?";
            int deletedRows = jdbcTemplate.update(sql, oneWeekAgo);

            log.info("[배치 성공] 오랜된 확인 알림 삭제 작업 완료. 삭제된 개수: {}", deletedRows);

            return RepeatStatus.FINISHED;
        });
    }
}
