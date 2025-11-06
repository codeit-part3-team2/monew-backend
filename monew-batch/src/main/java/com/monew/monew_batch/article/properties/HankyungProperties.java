package com.monew.monew_batch.article.properties;

import com.monew.monew_batch.article.enums.ArticleSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "rss.hankyung")
public class HankyungProperties {
    private final List<String> feeds;
    private final ArticleSource articleSource = ArticleSource.HANKYUNG;
}
