package com.monew.monew_batch.article.config;

import com.monew.monew_api.interest.entity.Keyword;
import com.monew.monew_batch.article.dto.ArticleKeywordPair;
import com.monew.monew_batch.article.job.processor.HankyungArticleItemProcessor;
import com.monew.monew_batch.article.job.ArticleItemReader;
import com.monew.monew_batch.article.job.ArticleItemWriter;
import com.monew.monew_batch.article.properties.HankyungProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@EnableConfigurationProperties(HankyungProperties.class)
public class HankyungJobConfig {

    private final ArticleItemReader reader; // 기존 KeywordReader 재사용
    private final HankyungArticleItemProcessor processor;
    private final ArticleItemWriter writer;

    @Bean
    public Job hankyungRssJob(JobRepository jobRepository, Step hankyungRssStep) {
        return new JobBuilder("hankyungRssJob", jobRepository)
                .start(hankyungRssStep)
                .build();
    }

    @Bean
    public Step hankyungRssStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("hankyungRssStep", jobRepository)
                .<Keyword, List<ArticleKeywordPair>>chunk(1, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean(name = "hankyungTaskExecutor")
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("hankyung-news-thread-");
        executor.setConcurrencyLimit(2);
        return executor;
    }
}