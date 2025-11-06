package com.monew.monew_api.article.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 기사 조회 기록 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleViewDto {

    private Long id;                            // 조회 기록 ID
    private Long viewedBy;                      // 조회한 사용자 ID
    private LocalDateTime createdAt;            // 조회 시각
    private Long articleId;                     // 기사 ID
    private String source;                      // 기사 출처
    private String sourceUrl;                   // 기사 원본 URL
    private String articleTitle;                // 기사 제목
    private LocalDateTime articlePublishedDate; // 기사 발행일
    private String articleSummary;              // 기사 요약
    private int articleCommentCount;            // 댓글 수
    private int articleViewCount;               // 조회 수
}