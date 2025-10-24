package com.monew.monew_api.article.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 커서 기반 페이지 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursorPageResponseArticleDto<T> {

    private List<T> content;            // 페이지 데이터
    private String nextCursor;          // 다음 커서 값
    private LocalDateTime nextAfter;    // 커서 기준 다음 시각
    private int size;                   // 요청한 페이지 크기
    private long totalElements;         // 전체 데이터 수
    private boolean hasNext;            // 다음 페이지 여부
}