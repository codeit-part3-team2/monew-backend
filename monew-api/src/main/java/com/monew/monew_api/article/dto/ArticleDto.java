package com.monew.monew_api.article.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 단일 기사 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
public class ArticleDto {

    private Long id;                    // 기사 ID
    private String source;              // 출처
    private String sourceUrl;           // 원본 URL
    private String title;               // 제목
    private LocalDateTime publishDate;  // 발행일
    private String summary;             // 요약
    private int commentCount;           // 댓글 수
    private int viewCount;              // 조회 수
    private boolean viewedByMe;         // 내가 조회했는지 여부

    @QueryProjection
    public ArticleDto(
            Long id, String source, String sourceUrl,
            String title, LocalDateTime publishDate, String summary,
            int commentCount, int viewCount, boolean viewedByMe
    ) {
        this.id = id;
        this.source = source;
        this.sourceUrl = sourceUrl;
        this.title = title;
        this.publishDate = publishDate;
        this.summary = summary;
        this.commentCount = commentCount;
        this.viewCount = viewCount;
        this.viewedByMe = viewedByMe;
    }
}
