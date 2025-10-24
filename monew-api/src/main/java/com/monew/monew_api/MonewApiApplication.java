package com.monew.monew_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MonewApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MonewApiApplication.class, args);
	}

}
