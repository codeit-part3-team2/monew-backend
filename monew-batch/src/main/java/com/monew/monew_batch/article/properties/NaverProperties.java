package com.monew.monew_batch.article.properties;

import com.monew.monew_batch.article.enums.ArticleSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "naver")
public class NaverProperties {
    private final String url;
    private final String clientId;
    private final String clientSecret;
    private final ArticleSource articleSource = ArticleSource.NAVER;
}
