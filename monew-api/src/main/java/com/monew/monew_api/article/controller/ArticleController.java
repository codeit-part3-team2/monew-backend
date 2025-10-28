package com.monew.monew_api.article.controller;

import com.monew.monew_api.article.dto.ArticleDto;
import com.monew.monew_api.article.dto.ArticleViewDto;
import com.monew.monew_api.article.dto.CursorPageResponseArticleDto;
import com.monew.monew_api.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleController {

    private final String DEFAULT_ARTICLE_SOURCE = "Naver";

    private final ArticleService articleService;

    @PostMapping("/{articleId}/article-views")
    public ResponseEntity<ArticleViewDto> viewArticle(
            @PathVariable Long articleId,
            @RequestHeader("Monew-Request-User-ID") Long userId
    ) {
        ArticleViewDto dto = articleService.recordArticleView(articleId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @GetMapping
    public ResponseEntity<CursorPageResponseArticleDto<ArticleDto>> getArticles(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long interestId,
            @RequestParam(required = false) List<String> sourceIn,
            //
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime publishDateFrom,
            //
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime publishDateTo,
            //
            @RequestParam(defaultValue = "publishDate") String orderBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false)
            //
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime after,
            //
            @RequestParam(defaultValue = "10") int limit,
            @RequestHeader("Monew-Request-User-ID") Long userId
    ) {
        if (sourceIn == null || sourceIn.isEmpty()) {
            sourceIn = List.of(DEFAULT_ARTICLE_SOURCE);
        }

        System.out.println("publishDateFrom: " + publishDateFrom);
        System.out.println("publishDateTo: " + publishDateTo);

        LocalDateTime now = LocalDateTime.now();
        if (publishDateFrom == null) {
            publishDateFrom = now.minusDays(7);
        }

        if (publishDateTo == null) {
            publishDateTo = now;
        }

        CursorPageResponseArticleDto<ArticleDto> dto = articleService.getArticles(
                keyword, interestId, sourceIn,
                publishDateFrom, publishDateTo,
                orderBy, direction,
                cursor, after, limit, userId
        );

        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleDto> getArticleById(
            @PathVariable Long articleId,
            @RequestHeader("Monew-Request-User-ID") Long userId
    ) {
        ArticleDto dto = articleService.findArticle(articleId, userId);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    @GetMapping("/sources")
    public ResponseEntity<List<String>> getSources() {
        List<String> sources = articleService.getAllSources();
        return ResponseEntity.status(HttpStatus.OK).body(sources);
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long articleId) {
        articleService.softDeleteArticle(articleId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{articleId}/hard")
    public ResponseEntity<Void> hardDeleteArticle(@PathVariable Long articleId) {
        articleService.hardDeleteArticle(articleId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}