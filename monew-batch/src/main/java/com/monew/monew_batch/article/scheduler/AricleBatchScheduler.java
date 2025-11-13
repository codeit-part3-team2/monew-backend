package com.monew.monew_batch.article.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableScheduling
public class AricleBatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job naverNewsJob;
    private final Job chosunRssJob;
    private final Job hankyungRssJob;
    private final Job yonhapRssJob;

    public AricleBatchScheduler(
            JobLauncher jobLauncher,
            @Qualifier("naverNewsJob") Job naverNewsJob,
            @Qualifier("chosunRssJob") Job chosunRssJob,
            @Qualifier("hankyungRssJob") Job hankyungRssJob,
            @Qualifier("yonhapRssJob") Job yonhapRssJob
    ) {
        this.jobLauncher = jobLauncher;
        this.naverNewsJob = naverNewsJob;
        this.chosunRssJob = chosunRssJob;
        this.hankyungRssJob = hankyungRssJob;
        this.yonhapRssJob = yonhapRssJob;
    }

    @Scheduled(cron = "0 0 * * * *", zone = "Asia/Seoul")
//    @Scheduled(fixedRate = 600000) // 테스트용
    public void runJob() throws Exception {
        log.info("🕒 [Batch Scheduler] 뉴스 수집 Job 실행");

        JobParameters params = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        jobLauncher.run(naverNewsJob, params);
        jobLauncher.run(chosunRssJob, params);
        jobLauncher.run(yonhapRssJob, params);
//        jobLauncher.run(hankyungRssJob, params); // 한경은 불안정함(엄격한 속도 제한과 아이피 제한), 사용 불가능
    }
}