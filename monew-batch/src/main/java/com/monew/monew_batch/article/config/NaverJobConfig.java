package com.monew.monew_batch.article.config;

import com.monew.monew_api.interest.entity.Keyword;
import com.monew.monew_batch.article.dto.ArticleKeywordPair;
import com.monew.monew_batch.article.job.ArticleNotificationRequestListener;
import com.monew.monew_batch.article.job.processor.NaverArticleItemProcessor;
import com.monew.monew_batch.article.job.ArticleItemReader;
import com.monew.monew_batch.article.job.ArticleItemWriter;
import com.monew.monew_batch.article.properties.NaverProperties;
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
@EnableConfigurationProperties(NaverProperties.class)
public class NaverJobConfig {

    private final ArticleItemReader reader;
    private final NaverArticleItemProcessor processor;
    private final ArticleItemWriter writer;
    private final ArticleNotificationRequestListener listener;

    @Bean
    public Job naverNewsJob(JobRepository jobRepository, Step naverNewsStep) {
        return new JobBuilder("naverNewsJob", jobRepository)
                .start(naverNewsStep)
                .listener(listener)
                .build();
    }

    @Bean
    public Step naverNewsStep(JobRepository jobRepository,
                              PlatformTransactionManager transactionManager) {
        return new StepBuilder("naverNewsStep", jobRepository)
                .<Keyword, List<ArticleKeywordPair>>chunk(1, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean(name = "naverTaskExecutor")
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("naver-news-thread-");
        executor.setConcurrencyLimit(2);
        return executor;
    }

}
