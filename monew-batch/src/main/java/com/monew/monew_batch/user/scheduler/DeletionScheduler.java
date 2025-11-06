package com.monew.monew_batch.user.scheduler;

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
public class DeletionScheduler {

    private final JobLauncher jobLauncher;
    private final Job userDeletionJob;

    /**
     * [요구사항] Soft delete 후 1일 경과한 사용자를 영구 삭제
     * [프로토타입] 5초마다 체크하여 5분 경과한 사용자 삭제
     */
    // @Scheduled(fixedDelay = 50000)
    @Scheduled(cron = "0 10 5 * * *", zone = "Asia/Seoul")
    public void runUserDeletionJob() throws Exception {
        log.info("==== Starting User Deletion Job ====");

        JobParameters parameters = new JobParametersBuilder()
                .addLong("runTime", System.currentTimeMillis())
                .toJobParameters();

        JobExecution exec = jobLauncher.run(userDeletionJob, parameters);

        log.info("==== User Deletion Job Finished ====");
        log.info("Status            : {}", exec.getStatus());
        log.info("Exit Status       : {}", exec.getExitStatus());
        log.info("Job Instance ID   : {}", exec.getJobId());
        log.info("Job Create Time   : {}", exec.getCreateTime());
        log.info("Job End Time      : {}", exec.getEndTime());
        log.info("Last Updated      : {}", exec.getLastUpdated());
        log.info("Failure Exceptions: {}", exec.getFailureExceptions());
    }
}
