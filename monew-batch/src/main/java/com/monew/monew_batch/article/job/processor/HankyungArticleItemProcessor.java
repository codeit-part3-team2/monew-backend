package com.monew.monew_batch.article.job.processor;

import com.monew.monew_api.article.entity.Article;
import com.monew.monew_api.interest.entity.Keyword;
import com.monew.monew_batch.article.dto.ArticleKeywordPair;
import com.monew.monew_batch.article.properties.HankyungProperties;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class HankyungArticleItemProcessor implements ItemProcessor<Keyword, List<ArticleKeywordPair>> {

    private final HankyungProperties properties;

    @Override
    public List<ArticleKeywordPair> process(Keyword keyword) {
        String keywordText = keyword.getKeyword();
        log.info("[한국경제 RSS] '{}' 관련 뉴스 수집 시작", keywordText);

        List<ArticleKeywordPair> pairs = new ArrayList<>();

        for (String feedUrl : properties.getFeeds()) {
            try {
                SyndFeed feed = fetchFeed(feedUrl);
                int totalEntries = feed.getEntries().size();
                int matchedCount = processFeedEntries(feed, keywordText, keyword, pairs);

                log.info("[한국경제 RSS] {} 에서 전체 {}건 중 {}건 매칭", feedUrl, totalEntries, matchedCount);

            } catch (Exception e) {
                log.warn("[한국경제 RSS] 피드 파싱 실패: {}", feedUrl, e);
            }
        }

        log.info("[한국경제 RSS] '{}' 관련 뉴스 최종 {}건 수집 완료", keywordText, pairs.size());
        return pairs.isEmpty() ? Collections.emptyList() : pairs;
    }

    /** RSS 피드를 불러와 SyndFeed 객체로 반환 */
    private SyndFeed fetchFeed(String feedUrl) throws Exception {
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(800); // 요청 간 간격 확보
                URLConnection connection = new URL(feedUrl).openConnection();
                connection.setRequestProperty("User-Agent", "MoNewBatchBot/1.0 (+https://monew.com)");
                return new SyndFeedInput().build(new XmlReader(connection));
            } catch (IOException e) {
                if (i < 2) {
                    log.warn("[한국경제 RSS] 요청 제한 (429) 발생 - 재시도 {}회차: {}", i + 1, feedUrl);
                    Thread.sleep(1000);
                } else {
                    throw e;
                }
            }
        }
        return null;
    }

    /** 하나의 피드 내 모든 엔트리를 순회하며 필터링 및 수집 */
    private int processFeedEntries(SyndFeed feed, String keywordText, Keyword keyword, List<ArticleKeywordPair> pairs) {
        int before = pairs.size();

        feed.getEntries().forEach(entry -> {
            String title = safeText(entry.getTitle());
            String desc = extractDescription(entry);

            // 본문이 비어있으면 스킵
            if (desc.isBlank()) return;

            // 제목 또는 본문에 키워드 포함 시만 수집
            if (!containsKeyword(title, desc, keywordText)) return;

            String link = Optional.ofNullable(entry.getLink()).orElse("");
            LocalDateTime pubDate = parsePublishedDate(entry.getPublishedDate());

            String articleSourceName = properties.getArticleSource().name();
            Article article = new Article(articleSourceName, link, title, pubDate, desc);
            pairs.add(new ArticleKeywordPair(article, keyword));
        });

        return pairs.size() - before;
    }

    private boolean containsKeyword(String title, String desc, String keyword) {
        String lower = keyword.toLowerCase();
        return title.toLowerCase().contains(lower) || desc.toLowerCase().contains(lower);
    }

    private String extractDescription(com.rometools.rome.feed.synd.SyndEntry entry) {
        return Optional.ofNullable(entry.getDescription())
                .map(d -> cleanText(d.getValue()))
                .orElse("")
                .trim();
    }

    private String safeText(String text) {
        return Optional.ofNullable(text).orElse("").trim();
    }

    private String cleanText(String text) {
        return text.replaceAll("<[^>]*>", "").trim();
    }

    private LocalDateTime parsePublishedDate(java.util.Date publishedDate) {
        return Optional.ofNullable(publishedDate)
                .map(d -> d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .orElse(LocalDateTime.now());
    }
}