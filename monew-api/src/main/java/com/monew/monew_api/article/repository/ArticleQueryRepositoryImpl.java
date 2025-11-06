package com.monew.monew_api.article.repository;

import com.monew.monew_api.article.dto.ArticleDto;
import com.monew.monew_api.article.dto.CursorPageResponseArticleDto;
import com.monew.monew_api.article.dto.QArticleDto;
import com.monew.monew_api.article.entity.Article;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

import static com.monew.monew_api.article.entity.QArticle.article;
import static com.monew.monew_api.article.entity.QArticleView.articleView;
import static com.monew.monew_api.article.entity.QInterestArticles.interestArticles;

@Slf4j
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
        List<ArticleDto> articles = queryFactory
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
                        publishDateBetween(publishDateFrom, publishDateTo),
                        cursorCondition(cursor, orderBy, direction)
                )
                .orderBy(order(orderBy, direction))
                .limit(limit + 1)
                .fetch();

        boolean hasNext = articles.size() > limit;
        if (hasNext) articles.remove(limit);

        ArticleDto last = hasNext ? articles.get(articles.size() - 1) : null;

        return CursorPageResponseArticleDto.<ArticleDto>builder()
                .content(articles)
                .nextCursor(last != null ? String.valueOf(last.getId()) : null)
                .nextAfter(null)
                .size(limit)
                .hasNext(hasNext)
                .build();
    }

    private BooleanExpression keywordContains(String keyword) {
        if (keyword == null || keyword.isBlank()) return null;
        return article.title.containsIgnoreCase(keyword)
                .or(article.summary.containsIgnoreCase(keyword));
    }

    // 추후 읽기 성능이 중요해진다 생각되면 join으로 변경 가능
    private BooleanExpression interestEq(Long interestId) {
        if (interestId == null) return null;
        return article.id.in(
                JPAExpressions.select(interestArticles.article.id)
                        .from(interestArticles)
                        .where(interestArticles.interest.id.eq(interestId))
        );
    }

    private BooleanExpression sourceIn(List<String> sourceIn) {
        if (sourceIn == null || sourceIn.isEmpty()) return null;
        return article.source.in(sourceIn);
    }

    private BooleanExpression publishDateBetween(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) return null;
        return article.publishDate.between(from, to);
    }

    // Java 14 이상에서 도입된 Switch Expression 문법 도입
    private OrderSpecifier<?>[] order(String orderBy, String direction) {
        boolean asc = "ASC".equalsIgnoreCase(direction);

        return switch (orderBy) {
            case "commentCount" -> asc
                    ? new OrderSpecifier[]{article.commentCount.asc(), article.id.asc()}
                    : new OrderSpecifier[]{article.commentCount.desc(), article.id.desc()};
            case "viewCount" -> asc
                    ? new OrderSpecifier[]{article.viewCount.asc(), article.id.asc()}
                    : new OrderSpecifier[]{article.viewCount.desc(), article.id.desc()};
            default -> asc
                    ? new OrderSpecifier[]{article.publishDate.asc(), article.id.asc()}
                    : new OrderSpecifier[]{article.publishDate.desc(), article.id.desc()};
        };
    }

    // after는 사실상 무쓸모, 즉 정렬 기준을 사용해야함.
    private BooleanExpression cursorCondition(
            String cursor, String orderBy, String direction) {

        if (cursor == null) return null;

        boolean desc = "DESC".equalsIgnoreCase(direction);
        Long cursorId = Long.valueOf(cursor);

        Article cursorArticle = queryFactory
                .selectFrom(article)
                .where(article.id.eq(cursorId))
                .fetchOne();

        if (cursorArticle == null) return null;

        return switch (orderBy) {
            case "commentCount" -> {
                int afterComment = cursorArticle.getCommentCount();
                yield desc
                        ? article.commentCount.lt(afterComment)
                        .or(article.commentCount.eq(afterComment)
                                .and(article.id.lt(cursorId)))
                        : article.commentCount.gt(afterComment)
                        .or(article.commentCount.eq(afterComment)
                                .and(article.id.gt(cursorId)));
            }

            case "viewCount" -> {
                int afterView = cursorArticle.getViewCount();
                yield desc
                        ? article.viewCount.lt(afterView)
                        .or(article.viewCount.eq(afterView)
                                .and(article.id.lt(cursorId)))
                        : article.viewCount.gt(afterView)
                        .or(article.viewCount.eq(afterView)
                                .and(article.id.gt(cursorId)));
            }

            default -> {
                LocalDateTime afterDate = cursorArticle.getPublishDate();
                yield desc
                        ? article.publishDate.lt(afterDate)
                        .or(article.publishDate.eq(afterDate)
                                .and(article.id.lt(cursorId)))
                        : article.publishDate.gt(afterDate)
                        .or(article.publishDate.eq(afterDate)
                                .and(article.id.gt(cursorId)));
            }
        };
    }
}