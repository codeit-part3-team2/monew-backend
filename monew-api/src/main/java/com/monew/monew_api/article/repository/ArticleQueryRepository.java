package com.monew.monew_api.article.repository;

import com.monew.monew_api.article.dto.ArticleDto;
import com.monew.monew_api.article.dto.CursorPageResponseArticleDto;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleQueryRepository {

    CursorPageResponseArticleDto<ArticleDto> searchArticles(
            String keyword, Long interestId, List<String> sourceIn,
            LocalDateTime publishDateFrom, LocalDateTime publishDateTo,
            String orderBy, String direction,
            String cursor, LocalDateTime after, int limit, Long userId
    );
}
