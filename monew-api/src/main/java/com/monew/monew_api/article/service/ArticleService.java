package com.monew.monew_api.article.service;

import com.monew.monew_api.article.dto.ArticleDto;
import com.monew.monew_api.article.dto.ArticleViewDto;
import com.monew.monew_api.article.dto.CursorPageResponseArticleDto;
import com.monew.monew_api.article.entity.Article;
import com.monew.monew_api.article.entity.ArticleView;
import com.monew.monew_api.article.repository.ArticleRepository;
import com.monew.monew_api.article.repository.ArticleViewRepository;
import com.monew.monew_api.common.exception.article.ArticleAlreadyViewedException;
import com.monew.monew_api.common.exception.article.ArticleNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleViewRepository articleViewRepository;

    /**
     * 기사 조회 기록 등록
     */
    @Transactional
    public ArticleViewDto recordArticleView(Long articleId, Long userId) {
        log.info("[기사 조회 기록 시도] 기사 ID: {}, 사용자 ID: {}", articleId, userId);

        // 이미 조회한 기사라면 예외 발생
        if (articleViewRepository.existsByUserIdAndArticleId(userId, articleId)) {
            log.warn("[조회 기록 실패] 이미 조회한 기사입니다. 사용자 ID: {}, 기사 ID: {}", userId, articleId);
            throw new ArticleAlreadyViewedException();
        }

        // 기사 존재 여부 확인
        Article article = articleRepository.findByIdAndIsDeletedFalse(articleId)
                .orElseThrow(() -> {
                    log.warn("[조회 기록 실패] 존재하지 않는 기사: {}", articleId);
                    return new ArticleNotFoundException();
                });

        // 조회 기록 생성
        ArticleView articleView = new ArticleView(userId, articleId);
        ArticleView saved = articleViewRepository.save(articleView);
        log.info("[조회 기록 성공] 기사 ID: {}, 사용자 ID: {}", articleId, userId);

        return ArticleViewDto.builder()
                .id(saved.getId())
                .viewedBy(userId)
                .createdAt(saved.getCreatedAt())
                .articleId(articleId)
                .source(article.getSource())
                .sourceUrl(article.getSourceUrl())
                .articleTitle(article.getTitle())
                .articlePublishedDate(article.getPublishDate())
                .articleSummary(article.getSummary())
                .articleCommentCount(article.getCommentCount())
                .articleViewCount(article.getViewCount() + 1)
                .build();
    }

    /**
     * 기사 목록 조회 (검색/필터/페이징 포함)
     */
    public CursorPageResponseArticleDto<ArticleDto> getArticles(
            String keyword, Long interestId, List<String> sourceIn,
            LocalDateTime publishDateFrom, LocalDateTime publishDateTo,
            String orderBy, String direction,
            String cursor, LocalDateTime after, int limit, Long userId
    ) {
        log.info("[기사 목록 조회] 사용자 ID: {}, 키워드: {}, 관심사 ID: {}", userId, keyword, interestId);

        CursorPageResponseArticleDto<ArticleDto> result = articleRepository.searchArticles(
                keyword,
                interestId,
                sourceIn,
                publishDateFrom,
                publishDateTo,
                orderBy,
                direction,
                cursor,
                after,
                limit,
                userId
        );

        log.info("[기사 목록 조회 완료] 조회된 기사 수: {}", result.getContent().size());
        return result;
    }

    /**
     * 단일 기사 상세 조회
     */
    public ArticleDto findArticle(Long articleId, Long userId) {
        log.info("[기사 상세 조회 시도] 기사 ID: {}, 사용자 ID: {}", articleId, userId);

        Article article = articleRepository.findByIdAndIsDeletedFalse(articleId)
                .orElseThrow(() -> {
                    log.warn("[기사 상세 조회 실패] 존재하지 않는 기사: {}", articleId);
                    return new ArticleNotFoundException();
                });

        boolean viewedByMe = articleViewRepository.existsByUserIdAndArticleId(userId, articleId);
        log.debug("[기사 상세 조회 성공] 기사 ID: {}, 사용자 ID: {}, 조회 여부: {}", articleId, userId, viewedByMe);

        return ArticleDto.builder()
                .id(article.getId())
                .source(article.getSource())
                .sourceUrl(article.getSourceUrl())
                .title(article.getTitle())
                .publishDate(article.getPublishDate())
                .summary(article.getSummary())
                .viewCount(article.getViewCount())
                .viewedByMe(viewedByMe)
                .build();
    }

    /**
     * 전체 뉴스 소스 목록 조회
     */
    public List<String> getAllSources() {
        log.info("[뉴스 출처 목록 조회]");
        List<String> sources = articleRepository.findDistinctSources();
        log.debug("[뉴스 출처 조회 완료] 출처 개수: {}", sources.size());
        return sources;
    }

    /**
     * 기사 논리 삭제
     */
    @Transactional
    public void softDeleteArticle(Long articleId) {
        log.info("[기사 논리 삭제 시도] 기사 ID: {}", articleId);

        Article article = articleRepository.findByIdAndIsDeletedFalse(articleId)
                .orElseThrow(() -> {
                    log.warn("[논리 삭제 실패] 존재하지 않는 기사: {}", articleId);
                    return new ArticleNotFoundException();
                });

        article.softDelete();
        log.info("[논리 삭제 성공] 기사 ID: {}", articleId);
    }

    /**
     * 기사 영구 삭제
     */
    @Transactional
    public void hardDeleteArticle(Long articleId) {
        log.info("[기사 영구 삭제 시도] 기사 ID: {}", articleId);

        articleRepository.deleteById(articleId);
        log.warn("[기사 영구 삭제 완료] 기사 ID: {}", articleId);
    }
}