package com.monew.monew_api.useractivity.repository;

import com.monew.monew_api.article.entity.ArticleView;
import com.monew.monew_api.useractivity.dto.ArticleViewActivityDto;
import com.monew.monew_api.useractivity.tempEntity.Comment;
import com.monew.monew_api.useractivity.tempEntity.CommentLike;
import com.monew.monew_api.useractivity.tempEntity.Subscription;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.monew.monew_api.article.entity.QArticle.article;
import static com.monew.monew_api.article.entity.QArticleView.articleView;
import static com.monew.monew_api.useractivity.tempEntity.QComment.comment;
import static com.monew.monew_api.useractivity.tempEntity.QCommentLike.commentLike;
import static com.monew.monew_api.useractivity.tempEntity.QInterestsKeywords.interestsKeywords;
import static com.monew.monew_api.useractivity.tempEntity.QKeywordEntity.keywordEntity;
import static com.monew.monew_api.useractivity.tempEntity.QSubscription.subscription;
import static com.monew.monew_api.article.entity.QInterest.interest;
import static com.monew.monew_api.article.entity.QArticleView.articleView;
import static com.monew.monew_api.domain.user.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserActivityRepositoryImpl implements UserActivityRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    @Override
    public List<Subscription> findSubscriptionsByUserId(Long userId) {
        return queryFactory
                .selectFrom(subscription)
                .join(subscription.interest, interest).fetchJoin()
                .leftJoin(interest.interestsKeywords, interestsKeywords).fetchJoin()
                .leftJoin(interestsKeywords.keywordEntity, keywordEntity).fetchJoin()
                .where(subscription.userId.eq(userId))
                .distinct()
                .fetch();
    }

    @Override
    public List<Comment> findRecentCommentsByUserId(Long userId) {
        return queryFactory
                .selectFrom(comment)
                .join(comment.article, article).fetchJoin()
                .join(comment.user, user).fetchJoin()
                .where(
                        comment.userId.eq(userId),
                        comment.isDeleted.isFalse(),
                        article.isDeleted.isFalse(),
                        user.deletedAt.isNull()
                )
                .orderBy(comment.createdAt.desc())
                .limit(10)
                .fetch();
    }

    @Override
    public List<CommentLike> findRecentLikesByUserId(Long userId) {
        return queryFactory
                .selectFrom(commentLike)
                .join(commentLike.comment, comment).fetchJoin()
                .join(comment.article, article).fetchJoin()
                .join(comment.user, user).fetchJoin()
                .where(
                        commentLike.userId.eq(userId),
                        comment.isDeleted.eq(false),
                        article.isDeleted.eq(false),
                        user.deletedAt.isNull()
                )
                .orderBy(commentLike.createdAt.desc())
                .limit(10)
                .fetch();
    }

    @Override
    public List<ArticleViewActivityDto> findRecentViewsByUserId(Long userId) {
        return queryFactory
                .select(Projections.fields(
                        ArticleViewActivityDto.class,
                        articleView.id.stringValue().as("id"),
                        articleView.userId.stringValue().as("viewedBy"),
                        articleView.createdAt.as("createdAt"),
                        articleView.articleId.stringValue().as("articleId"),
                        article.source.as("source"),
                        article.sourceUrl.as("sourceUrl"),
                        article.title.as("articleTitle"),
                        article.publishDate.as("articlePublishedDate"),
                        article.summary.as("articleSummary"),
                        article.commentCount.as("articleCommentCount"),
                        article.viewCount.as("articleViewCount")
                ))
                .from(articleView)
                .join(article).on(article.id.eq(articleView.articleId))
                .where(
                        articleView.userId.eq(userId),
                        article.isDeleted.eq(false)
                )
                .orderBy(articleView.createdAt.desc())
                .limit(10)
                .fetch();
    }

    @Override
    public Object[] findUserActivitiesByUserId(Long userId) {
        String sql = """
            WITH recent_subscriptions AS (
                SELECT
                    s.id AS subscription_id,
                    s.user_id,
                    s.created_at AS subscription_created_at,
                    i.id AS interest_id,
                    i.name AS interest_name,
                    i.subscriber_count,
                    STRING_AGG(k.keyword, ',') AS keywords
                FROM subscribes s
                JOIN interests i ON s.interest_id = i.id
                LEFT JOIN interests_keywords ik ON i.id = ik.interest_id
                LEFT JOIN keywords k ON ik.keyword_id = k.id
                WHERE s.user_id = :userId
                GROUP BY s.id, s.user_id, s.created_at, i.id, i.name, i.subscriber_count
                ORDER BY s.created_at DESC
            ),
            recent_comments AS (
                SELECT
                    c.id,
                    c.article_id,
                    c.user_id,
                    c.content,
                    c.like_count,
                    c.created_at,
                    a.title AS article_title,
                    u.nickname AS user_nickname
                FROM comments c
                JOIN articles a ON c.article_id = a.id
                JOIN users u ON c.user_id = u.id
                WHERE c.user_id = :userId
                  AND c.is_deleted = false
                  AND a.is_deleted = false
                  AND u.deleted_at IS NULL
                ORDER BY c.created_at DESC
                LIMIT 10
            ),
            recent_likes AS (
                SELECT
                    cl.id,
                    cl.user_id,
                    cl.created_at,
                    cl.comment_id,
                    c.content AS comment_content,
                    c.like_count AS comment_like_count,
                    c.created_at AS comment_created_at,
                    c.user_id AS comment_user_id,
                    u.nickname AS comment_user_nickname,
                    a.id AS article_id,
                    a.title AS article_title
                FROM comment_likes cl
                JOIN comments c ON cl.comment_id = c.id
                JOIN articles a ON c.article_id = a.id
                JOIN users u ON c.user_id = u.id
                WHERE cl.user_id = :userId
                  AND c.is_deleted = false
                  AND a.is_deleted = false
                  AND u.deleted_at IS NULL
                ORDER BY cl.created_at DESC
                LIMIT 10
            ),
            recent_views AS (
                SELECT
                    av.id,
                    av.user_id,
                    av.created_at,
                    av.article_id,
                    a.source,
                    a.source_url,
                    a.title AS article_title,
                    a.publish_date,
                    a.summary,
                    a.comment_count,
                    a.view_count
                FROM article_views av
                JOIN articles a ON av.article_id = a.id
                WHERE av.user_id = :userId
                  AND a.is_deleted = false
                ORDER BY av.created_at DESC
                LIMIT 10
            )
            SELECT
                u.id AS id,
                u.email,
                u.nickname,
                u.created_at AS created_at,
                COALESCE(
                    (SELECT jsonb_agg(to_jsonb(rs) ORDER BY rs.subscription_created_at DESC) 
                     FROM recent_subscriptions rs WHERE rs.user_id = u.id),
                    '[]'::jsonb
                ) AS subscriptions,
                COALESCE(
                    (SELECT jsonb_agg(to_jsonb(rc) ORDER BY rc.created_at DESC) 
                     FROM recent_comments rc WHERE rc.user_id = u.id),
                    '[]'::jsonb
                ) AS comments,
                COALESCE(
                    (SELECT jsonb_agg(to_jsonb(rl) ORDER BY rl.created_at DESC) 
                     FROM recent_likes rl WHERE rl.user_id = u.id),
                    '[]'::jsonb
                ) AS likes,
                COALESCE(
                    (SELECT jsonb_agg(to_jsonb(rv) ORDER BY rv.created_at DESC) 
                     FROM recent_views rv WHERE rv.user_id = u.id),
                    '[]'::jsonb
                ) AS views
            FROM users u
            WHERE u.id = :userId
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("userId", userId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();

        return results.isEmpty() ? null : results.get(0);
    }
}