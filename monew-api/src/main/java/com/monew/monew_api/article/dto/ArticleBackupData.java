package com.monew.monew_api.article.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.monew.monew_api.article.entity.Article;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 뉴스 백업 데이터 구조
 * - S3 저장용 DTO
 * - 기사 정보 + 연결된 키워드 목록 포함
 */
@Getter
@Setter
@NoArgsConstructor
public class ArticleBackupData {

    private LocalDateTime backupDate;
    private List<ArticleData> articles;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ArticleData {

        private String source;
        private String sourceUrl;
        private String title;
        private LocalDateTime publishDate;
        private String summary;

        @JsonProperty("keywords")
        private List<String> keywords;

        /**
         * QueryProjection 기반 생성자
         * - string_agg 결과 문자열을 List<String>으로 변환
         */
        @QueryProjection
        public ArticleData(String source, String sourceUrl, String title,
                           LocalDateTime publishDate, String summary, String keywordsRaw) {
            this.source = source;
            this.sourceUrl = sourceUrl;
            this.title = title;
            this.publishDate = publishDate;
            this.summary = summary;
            this.keywords = Arrays.stream(Optional.ofNullable(keywordsRaw).orElse("").split(","))
                    .map(String::trim)
                    .filter(s -> !s.isBlank())
                    .distinct()
                    .collect(Collectors.toList());
        }

        /** Entity 변환용 헬퍼 */
        public Article toEntity() {
            return new Article(source, sourceUrl, title, publishDate, summary);
        }
    }
}
