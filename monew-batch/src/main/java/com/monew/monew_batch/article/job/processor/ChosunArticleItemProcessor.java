package com.monew.monew_batch.article.job.processor;

import com.monew.monew_api.article.entity.Article;
import com.monew.monew_api.interest.entity.Keyword;
import com.monew.monew_batch.article.dto.ArticleKeywordPair;
import com.monew.monew_batch.article.properties.ChosunProperties;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChosunArticleItemProcessor implements ItemProcessor<Keyword, List<ArticleKeywordPair>> {

    private final ChosunProperties properties;

    @Override
    public List<ArticleKeywordPair> process(Keyword keyword) {
        String keywordText = keyword.getKeyword();
        log.info("[조선일보 RSS] '{}' 관련 뉴스 수집 시작", keywordText);

        List<ArticleKeywordPair> pairs = new ArrayList<>();

        for (String feedUrl : properties.getFeeds()) {
            try {
                SyndFeed feed = fetchFeed(feedUrl);
                int totalEntries = feed.getEntries().size();
                int matchedCount = processFeedEntries(feed, keywordText, keyword, pairs);

                log.info("[조선일보 RSS] {} 에서 전체 {}건 중 {}건 매칭", feedUrl, totalEntries, matchedCount);

            } catch (Exception e) {
                log.warn("[조선일보 RSS] 피드 파싱 실패: {}", feedUrl, e);
            }
        }

        log.info("[조선일보 RSS] '{}' 관련 뉴스 최종 {}건 수집 완료", keywordText, pairs.size());
        return pairs.isEmpty() ? Collections.emptyList() : pairs;
    }

    /** RSS 피드를 불러와 SyndFeed 객체로 반환 */
    private SyndFeed fetchFeed(String feedUrl) throws Exception {
        return new SyndFeedInput().build(new XmlReader(new URL(feedUrl)));
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

        return pairs.size() - before; // 매칭된 건수 반환
    }

    /** title 또는 description 중 하나라도 키워드 포함 여부 검사 */
    private boolean containsKeyword(String title, String desc, String keyword) {
        String lower = keyword.toLowerCase();
        return title.toLowerCase().contains(lower) || desc.toLowerCase().contains(lower);
    }

    /** description 추출 및 HTML 정리 */
    private String extractDescription(com.rometools.rome.feed.synd.SyndEntry entry) {
        return Optional.ofNullable(entry.getDescription())
                .map(d -> cleanText(d.getValue()))
                .orElse("") // description이 없으면 빈 문자열 반환 → processFeedEntries에서 스킵 처리됨
                .trim();
    }

    /** null-safe로 title 텍스트 반환 */
    private String safeText(String text) {
        return Optional.ofNullable(text).orElse("").trim();
    }

    /** HTML 태그 제거 후 공백 정리 */
    private String cleanText(String text) {
        return text.replaceAll("<[^>]*>", "").trim();
    }

    /** 발행일 파싱 (없을 경우 현재 시각 사용) */
    private LocalDateTime parsePublishedDate(java.util.Date publishedDate) {
        return Optional.ofNullable(publishedDate)
                .map(d -> d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .orElse(LocalDateTime.now());
    }
}