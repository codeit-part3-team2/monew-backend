package com.monew.monew_api.article.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleSearchRequest {

    @Size(max = 50, message = "검색어(keyword)는 최대 50자까지 입력할 수 있습니다.")
    private String keyword;

    private Long interestId;

    private List<String> sourceIn = List.of("Naver");

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime publishDateFrom = LocalDateTime.now().minusDays(7);

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime publishDateTo = LocalDateTime.now();

    @Pattern(regexp = "^(publishDate|viewCount|commentCount)$",
            message = "정렬 기준(orderBy)은 publishDate, viewCount, commentCount 중 하나여야 합니다.")
    private String orderBy = "publishDate";

    @Pattern(regexp = "^(ASC|DESC)$",
            message = "정렬 방향(direction)은 ASC 또는 DESC만 가능합니다.")
    private String direction = "DESC";

    private String cursor;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime after;

    @Min(value = 1, message = "limit은 1 이상이어야 합니다.")
    @Max(value = 50, message = "limit은 최대 50까지만 가능합니다.")
    private int limit = 10;
}
