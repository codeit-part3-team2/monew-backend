package com.monew.monew_batch.comments;

import java.time.LocalDateTime;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.monew.monew_batch.comments.service.CommentPurgeService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableBatchProcessing
@EnableConfigurationProperties(CommentPurgeProperties.class)
@RequiredArgsConstructor
public class CommentPurgeJobConfig {

	private final CommentPurgeService purgeService;
	private final CommentPurgeProperties props;

	@Bean
	public Job commentPurgeJob(JobRepository jobRepository, Step commentPurgeStep) {
		return new JobBuilder("commentPurgeJob", jobRepository)
			.start(commentPurgeStep)
			.build();
	}

	@Bean
	public Step commentPurgeStep(JobRepository jobRepository,
		PlatformTransactionManager transactionManager) {
		return new StepBuilder("commentPurgeStep", jobRepository)
			.tasklet((contribution, chunkContext) -> {
				LocalDateTime cutoff = LocalDateTime.now().minusMinutes(props.getRetentionMinutes());
				purgeService.purge(cutoff);
				return RepeatStatus.FINISHED;
			}, transactionManager)
			.build();
	}
}
