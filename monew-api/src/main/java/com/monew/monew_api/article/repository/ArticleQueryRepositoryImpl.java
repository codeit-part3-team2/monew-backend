package com.monew.monew_api.article.repository;

import com.monew.monew_api.article.dto.ArticleDto;
import com.monew.monew_api.article.dto.CursorPageResponseArticleDto;
import com.monew.monew_api.article.dto.QArticleDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.monew.monew_api.article.entity.QArticle.*;
import static com.monew.monew_api.article.entity.QInterestArticles.interestArticles;
import static com.monew.monew_api.article.entity.QArticleView.*;

@RequiredArgsConstructor
public class ArticleQueryRepositoryImpl implements ArticleQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public CursorPageResponseArticleDto<ArticleDto> searchArticles(
            String keyword, Long interestId, List<String> sourceIn,
            LocalDateTime publishDateFrom, LocalDateTime publishDateTo,
            String orderBy, String direction,
            String cursor, LocalDateTime after, int limit, Long userId
    ) {
        List<ArticleDto> articleDtos = queryFactory
                .select(new QArticleDto(
                        article.id,
                        article.source,
                        article.sourceUrl,
                        article.title,
                        article.publishDate,
                        article.summary,
                        article.commentCount,
                        article.viewCount,
                        JPAExpressions
                                .selectOne()
                                .from(articleView)
                                .where(
                                        articleView.articleId.eq(article.id)
                                                .and(articleView.userId.eq(userId))
                                )
                                .exists()
                ))
                .from(article)
                .where(
                        article.isDeleted.isFalse(),
                        keywordContains(keyword),
                        interestEq(interestId),
                        sourceIn(sourceIn),
                        publishDateBetween(publishDateFrom, publishDateTo)
                )
                .orderBy(order(orderBy, direction))
                .limit(limit + 1)
                .fetch();

        boolean hasNext = articleDtos.size() > limit;
        if (hasNext) {
            articleDtos.remove(limit);
        }

        String nextCursor = hasNext ? String.valueOf(articleDtos.get(articleDtos.size() - 1).getId()) : null;
        LocalDateTime nextAfter = hasNext ? articleDtos.get(articleDtos.size() - 1).getPublishDate() : null;

        return CursorPageResponseArticleDto.<ArticleDto>builder()
                .content(articleDtos)
                .nextCursor(nextCursor)
                .nextAfter(nextAfter)
                .size(limit)
                .hasNext(hasNext)
                .build();
    }

    private BooleanExpression keywordContains(String keyword) {
        return (keyword == null || keyword.isBlank())
                ? null
                : article.title.containsIgnoreCase(keyword)
                .or(article.summary.containsIgnoreCase(keyword));
    }

    private BooleanExpression interestEq(Long interestId) {
        if (interestId == null) return null;

        return article.id.in(
                JPAExpressions
                        .select(interestArticles.article.id)
                        .from(interestArticles)
                        .where(interestArticles.interest.id.eq(interestId))
        );
    }

    private BooleanExpression sourceIn(List<String> sourceIn) {
        return article.source.in(sourceIn);
    }

    private BooleanExpression publishDateBetween(LocalDateTime from, LocalDateTime to) {
        return article.publishDate.between(from, to);
    }

    private OrderSpecifier<?> order(String orderBy, String direction) {
        OrderSpecifier<?> order;
        switch (orderBy) {
            case "commentCount" -> order = direction.equalsIgnoreCase("ASC")
                        ? article.commentCount.asc() : article.commentCount.desc();
            case "viewCount" -> order = direction.equalsIgnoreCase("ASC")
                        ? article.viewCount.asc() : article.viewCount.desc();
            default -> order = direction.equalsIgnoreCase("ASC")
                        ? article.publishDate.asc() : article.publishDate.desc();
        }

        return order;
    }
}
