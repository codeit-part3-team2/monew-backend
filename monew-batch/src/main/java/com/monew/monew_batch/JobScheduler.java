package com.monew.monew_batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 테스트용
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JobScheduler {

    // job을 실행시켜줄 객체
    private final JobLauncher jobLauncher;
    private final Job exampleJob01; // 사용자가 만든 job

    @Scheduled(initialDelay = 1000, fixedRate = 5000)
    public void runJob() throws Exception {
        JobParameters parameters = new JobParametersBuilder()
                .addLong("ts", System.currentTimeMillis())
                .toJobParameters();

        JobExecution exec = jobLauncher.run(exampleJob01, parameters);

        // 실행 결과 로그
        log.info("==== Job Finished ====");
        log.info("Status            : {}", exec.getStatus());
        log.info("Exit Status       : {}", exec.getExitStatus());
        log.info("Job Instance ID   : {}", exec.getJobId());
        log.info("Job getCreateTime : {}", exec.getCreateTime());
        log.info("Job getEndTime    : {}", exec.getEndTime());
        log.info("Last Updated      : {}", exec.getLastUpdated());
        log.info("Failure Exceptions: {}", exec.getFailureExceptions());
    }
}
