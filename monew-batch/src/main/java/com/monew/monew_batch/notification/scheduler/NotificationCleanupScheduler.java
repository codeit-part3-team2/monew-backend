package com.monew.monew_batch.notification.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationCleanupScheduler {

    private final JobLauncher jobLauncher;
    private final Job deleteOldNotificationJob;

    // 한국 기준 오전 4시
    @Scheduled(cron = "0 0 19 * * *", zone = "UTC")
    public void runDeleteOldNotificationJob() {
        try {
            JobParameters parameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            log.info("[스케줄러] deleteOldNotificationJob 실행");

            JobExecution exec = jobLauncher.run(deleteOldNotificationJob, parameters);

            // 실행 결과 로그
            log.info("==== Job Finished ====");
            log.info("Status            : {}", exec.getStatus());
            log.info("Exit Status       : {}", exec.getExitStatus());
            log.info("Job Instance ID   : {}", exec.getJobId());
            log.info("Job getCreateTime : {}", exec.getCreateTime());
            log.info("Job getEndTime    : {}", exec.getEndTime());
            log.info("Last Updated      : {}", exec.getLastUpdated());
            log.info("Failure Exceptions: {}", exec.getFailureExceptions());
        } catch (Exception e) {
            log.error("[스케줄러] deleteOldNotificationJob 실행 중 오류 발생", e);
        }
    }
}
