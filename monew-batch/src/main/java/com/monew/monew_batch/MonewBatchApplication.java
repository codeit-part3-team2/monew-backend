package com.monew.monew_batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
		"com.monew.monew_batch",
		"com.monew.monew_api.common"
})
@EnableJpaRepositories(basePackages = "com.monew.monew_api.common.user.repository")
@EntityScan(basePackages = "com.monew.monew_api.common")
@EnableJpaAuditing
@EnableScheduling
public class MonewBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonewBatchApplication.class, args);
	}

}
