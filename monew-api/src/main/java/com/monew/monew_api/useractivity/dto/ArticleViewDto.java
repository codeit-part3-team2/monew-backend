package com.monew.monew_api.useractivity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 기사 조회 정보 DTO (임시)
 * TODO: 팀원 작업 완료 후 com.monew.monew_api.article.dto.ArticleViewDto로 교체
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleViewDto {

    private String id;
    private String viewedBy;
    private LocalDateTime createdAt;

    private String articleId;
    private String source;
    private String sourceUrl;
    private String articleTitle;
    private LocalDateTime articlePublishedDate;
    private String articleSummary;
    private Long articleCommentCount;
    private Long articleViewCount;
}