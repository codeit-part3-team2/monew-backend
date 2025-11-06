package com.monew.monew_batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication(
		scanBasePackages = {
				"com.monew.monew_batch",
				"com.monew.monew_api.article.repository",
		},
	exclude = {
		org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration.class,
		org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration.class
	}
)
@EntityScan(basePackages = "com.monew.monew_api")
@EnableJpaRepositories(basePackages = "com.monew.monew_api")
@EnableScheduling
@EnableJpaAuditing
@EnableAsync
public class MonewBatchApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		SpringApplication.run(MonewBatchApplication.class, args);
	}

}
