package com.monew.monew_api;

import com.monew.monew_api.common.config.AWSConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = MonewApiApplication.class)
class MonewApiApplicationTests {

	@MockBean
	private AWSConfig awsConfig;

	@Test
	void contextLoads() {}
}
