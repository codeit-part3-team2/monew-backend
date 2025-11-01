package com.monew.monew_batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EntityScan(basePackages = "com.monew.monew_api")
@EnableJpaRepositories(basePackages = "com.monew.monew_api")
@EnableScheduling
public class MonewBatchApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		SpringApplication.run(MonewBatchApplication.class, args);
	}

}
