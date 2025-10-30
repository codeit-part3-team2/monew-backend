package com.monew.monew_api.article.controller;

import com.monew.monew_api.article.dto.ArticleDto;
import com.monew.monew_api.article.dto.ArticleSearchRequest;
import com.monew.monew_api.article.dto.ArticleViewDto;
import com.monew.monew_api.article.dto.CursorPageResponseArticleDto;
import com.monew.monew_api.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 기사 조회 기록 등록
     */
    @PostMapping("/{articleId}/article-views")
    public ResponseEntity<ArticleViewDto> viewArticle(
            @PathVariable Long articleId,
            @RequestHeader("Monew-Request-User-ID") Long userId
    ) {
        log.info("[API 요청] POST /api/articles/{}/article-views - 기사 조회 기록 요청, 사용자 ID: {}", articleId, userId);
        ArticleViewDto dto = articleService.recordArticleView(articleId, userId);
        log.info("[API 응답] POST /api/articles/{}/article-views - 조회 기록 성공, 조회 기록 ID: {}", articleId, dto.getId());
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    /**
     * 기사 목록 조회 (검색/필터/페이징 포함)
     */
    @GetMapping
    public ResponseEntity<CursorPageResponseArticleDto<ArticleDto>> getArticles(
            @Validated @ModelAttribute ArticleSearchRequest request,
            @RequestHeader("Monew-Request-User-ID") Long userId
    ) {
        log.info("[API 요청] GET /api/articles - 기사 목록 조회 요청, 사용자 ID: {}, 키워드: {}, 관심사 ID: {}, 커서: {}, After: {}",
                userId, request.getKeyword(), request.getInterestId(), request.getCursor(), request.getAfter());
        CursorPageResponseArticleDto<ArticleDto> dto = articleService.getArticles(request, userId);
        log.info("[API 응답] GET /api/articles - 조회 성공, 반환된 기사 수: {}", dto.getContent().size());
        return ResponseEntity.ok(dto);
    }

    /**
     * 단일 기사 상세 조회
     */
    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleDto> getArticleById(
            @PathVariable Long articleId,
            @RequestHeader("Monew-Request-User-ID") Long userId
    ) {
        log.info("[API 요청] GET /api/articles/{} - 기사 상세 조회 요청, 사용자 ID: {}", articleId, userId);
        ArticleDto dto = articleService.findArticle(articleId, userId);

        if (!dto.isViewedByMe()) {
            articleService.recordArticleView(articleId, userId);
        }

        log.info("[API 응답] GET /api/articles/{} - 기사 상세 조회 성공", articleId);
        return ResponseEntity.status(HttpStatus.OK).body(dto);
    }

    /**
     * 기사 출처 목록 조회
     */
    @GetMapping("/sources")
    public ResponseEntity<List<String>> getSources() {
        log.info("[API 요청] GET /api/articles/sources - 뉴스 출처 목록 조회 요청");
        List<String> sources = articleService.getAllSources();
        log.info("[API 응답] GET /api/articles/sources - 뉴스 출처 목록 조회 성공, 개수: {}", sources.size());
        return ResponseEntity.status(HttpStatus.OK).body(sources);
    }

    /**
     * 기사 논리 삭제
     */
    @DeleteMapping("/{articleId}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long articleId) {
        log.info("[API 요청] DELETE /api/articles/{} - 기사 논리 삭제 요청", articleId);
        articleService.softDeleteArticle(articleId);
        log.info("[API 응답] DELETE /api/articles/{} - 기사 논리 삭제 성공", articleId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 기사 영구 삭제
     */
    @DeleteMapping("/{articleId}/hard")
    public ResponseEntity<Void> hardDeleteArticle(@PathVariable Long articleId) {
        log.info("[API 요청] DELETE /api/articles/{}/hard - 기사 영구 삭제 요청", articleId);
        articleService.hardDeleteArticle(articleId);
        log.info("[API 응답] DELETE /api/articles/{}/hard - 기사 영구 삭제 성공", articleId);
        return ResponseEntity.noContent().build();
    }

    // RSS 문제
    // 포폴

    // S3
    // S3
    // 로직 (A이베
}