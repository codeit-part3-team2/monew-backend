package com.monew.monew_batch.article.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class NaverNewsScheduler {

    private final JobLauncher jobLauncher;
    private final Job naverNewsJob;

//    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
    @Scheduled(fixedRate = 60000) // 테스트용
    public void runJob() throws Exception {
        log.info("🕒 [Batch Scheduler] 네이버 뉴스 수집 Job 실행");

        JobParameters params = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(naverNewsJob, params);
    }

}
