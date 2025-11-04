package com.monew.monew_api.article.event;

import java.time.LocalDateTime;

/**
 * 기사 조회 이벤트
 * 사용자가 기사를 조회했을 때 발행
 * Update 전략 사용
 * @param viewId
 * @param userId
 * @param createdAt
 * @param articleId
 * @param source
 * @param sourceUrl
 * @param articleTitle
 * @param articlePublishedDate
 * @param articleSummary
 * @param articleCommentCount
 * @param articleViewCount
 * @param occurredAt
 */
public record ArticleViewedEvent(
        Long viewId,
        Long userId,
        LocalDateTime createdAt,
        Long articleId,
        String source,
        String sourceUrl,
        String articleTitle,
        LocalDateTime articlePublishedDate,
        String articleSummary,
        Integer articleCommentCount,
        Integer articleViewCount,
        LocalDateTime occurredAt
) {
    public static ArticleViewedEvent of(
            Long id,
            Long userId,
            LocalDateTime createdAt,
            Long articleId,
            String source,
            String sourceUrl,
            String articleTitle,
            LocalDateTime articlePublishedDate,
            String articleSummary,
            Integer articleCommentCount,
            Integer articleViewCount
    ) {
        return new ArticleViewedEvent(
                id, userId, createdAt, articleId,
                source, sourceUrl, articleTitle,
                articlePublishedDate, articleSummary,
                articleCommentCount, articleViewCount,
                LocalDateTime.now()
        );
    }

    public Integer getDelta() {
        return +1;
    }
}