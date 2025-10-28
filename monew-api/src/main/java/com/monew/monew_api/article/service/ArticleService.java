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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleViewRepository articleViewRepository;

    @Transactional
    public ArticleViewDto recordArticleView(Long articleId, Long userId) {
        if (articleViewRepository.existsByUserIdAndArticleId(userId, articleId)) {
            throw new ArticleAlreadyViewedException();
        }

        Article article = articleRepository.findByIdAndIsDeletedFalse(articleId)
                .orElseThrow(ArticleNotFoundException::new);

        ArticleView articleView = new ArticleView();
        articleView.setUserId(userId);
        articleView.setArticleId(articleId);

        ArticleView saved = articleViewRepository.save(articleView);

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

    public CursorPageResponseArticleDto<ArticleDto> getArticles(
            String keyword, Long interestId, List<String> sourceIn,
            LocalDateTime publishDateFrom, LocalDateTime publishDateTo,
            String orderBy, String direction,
            String cursor, LocalDateTime after, int limit, Long userId
    ) {
        return articleRepository.searchArticles(
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
    }

    public ArticleDto findArticle(Long articleId, Long userId) {
        Article article = articleRepository.findByIdAndIsDeletedFalse(articleId)
                .orElseThrow(ArticleNotFoundException::new);

        boolean viewedByMe = articleViewRepository.existsByUserIdAndArticleId(userId, articleId);

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

    public List<String> getAllSources() {
        return articleRepository.findDistinctSources();
    }

    @Transactional
    public void softDeleteArticle(Long articleId) {
        Article article = articleRepository.findByIdAndIsDeletedFalse(articleId)
                .orElseThrow(ArticleNotFoundException::new);
        article.setDeleted(true);
    }

    @Transactional
    public void hardDeleteArticle(Long articleId) {
        articleRepository.deleteById(articleId);
    }
}
