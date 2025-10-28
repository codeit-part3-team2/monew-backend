package com.monew.monew_batch.comments;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableScheduling
public class CommentPurgeScheduler {

	private final JobLauncher jobLauncher;
	private final Job commentPurgeJob;

	@Scheduled(cron = "0 * * * * *") // 프로토타입: 1분마다
	public void runPurgeJob() throws Exception {
		JobParameters params = new JobParametersBuilder()
			.addLong("ts", System.currentTimeMillis())
			.toJobParameters();

		jobLauncher.run(commentPurgeJob, params);
	}

}
