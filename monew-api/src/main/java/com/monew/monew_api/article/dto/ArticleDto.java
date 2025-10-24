package com.monew.monew_api.article.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 단일 기사 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
