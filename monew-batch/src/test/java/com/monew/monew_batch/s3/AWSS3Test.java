package com.monew.monew_batch.s3;

import com.monew.monew_batch.common.config.AWSConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.UUID;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AWSS3Test {

  private String testKey;

  @Autowired
  private S3Client s3Client;

  @Autowired
  private AWSConfig config;

  @BeforeAll
  void setUp() {
    testKey = "test/" + UUID.randomUUID() + ".txt";
  }

  @Test
  @Order(1)
  void uploadFile() {
    s3Client.putObject(
            PutObjectRequest.builder()
                    .bucket(config.getBucket())
                    .key(testKey)
                    .build(),
            RequestBody.fromString("hello world")
    );
    log.info("업로드 성공: {}", testKey);
  }

  @Test
  @Order(2)
  void downloadFile() {
    String content = s3Client.getObjectAsBytes(
            GetObjectRequest.builder()
                    .bucket(config.getBucket())
                    .key(testKey)
                    .build()
    ).asUtf8String();

    Assertions.assertEquals("hello world", content);
    log.info("다운로드 성공: {}", content);
  }

  @AfterAll
  void cleanUp() {
    s3Client.deleteObject(
            DeleteObjectRequest.builder()
                    .bucket(config.getBucket())
                    .key(testKey)
                    .build()
    );
    log.info("테스트 파일 삭제 완료: {}", testKey);
  }
}