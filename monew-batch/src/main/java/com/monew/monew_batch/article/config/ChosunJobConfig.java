package com.monew.monew_batch.article.config;

import com.monew.monew_api.interest.entity.Keyword;
import com.monew.monew_batch.article.dto.ArticleKeywordPair;
import com.monew.monew_batch.article.job.processor.ChosunArticleItemProcessor;
import com.monew.monew_batch.article.job.ArticleItemReader;
import com.monew.monew_batch.article.job.ArticleItemWriter;
import com.monew.monew_batch.article.properties.ChosunProperties;
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
@EnableConfigurationProperties(ChosunProperties.class)
public class ChosunJobConfig {

    private final ArticleItemReader reader; // 키워드 재사용
    private final ChosunArticleItemProcessor processor;
    private final ArticleItemWriter writer;

    @Bean
    public Job chosunRssJob(JobRepository jobRepository, Step chosunRssStep) {
        return new JobBuilder("chosunRssJob", jobRepository)
                .start(chosunRssStep)
                .build();
    }

    @Bean
    public Step chosunRssStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("chosunRssStep", jobRepository)
                .<Keyword, List<ArticleKeywordPair>>chunk(1, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean(name = "chosunTaskExecutor")
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("chosun-news-thread-");
        executor.setConcurrencyLimit(2);
        return executor;
    }
}