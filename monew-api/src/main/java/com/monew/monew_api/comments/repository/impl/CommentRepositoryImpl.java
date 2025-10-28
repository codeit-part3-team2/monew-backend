package com.monew.monew_api.comments.repository.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.monew.monew_api.comments.entity.Comment;
import com.monew.monew_api.comments.entity.QComment;
import com.monew.monew_api.comments.repository.CommentRepositoryCustom;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;
	private final QComment c = QComment.comment;

	@Override
	public List<Comment> findPageByArticleIdOrderByCreatedAtDesc(Long articleId, Long cursorId,
		LocalDateTime cursorCreatedAt, int limit) {

		BooleanExpression byArticle = articleIdEq(articleId);
		BooleanExpression afterCursor = buildCreatedAtCursor(cursorId, cursorCreatedAt);

		return jpaQueryFactory
			.selectFrom(c)
			.leftJoin(c.user).fetchJoin()
			.leftJoin(c.article).fetchJoin()
			.where(byArticle, afterCursor)
			.orderBy(c.createdAt.desc(), c.id.desc())
			.limit(limit + 1)
			.fetch();
	}

	@Override
	public List<Comment> findPageByArticleIdOrderByLikeCountDesc(Long articleId, Long cursorId, Integer cursorLikeCount,
		int limit) {
		BooleanExpression byArticle = articleIdEq(articleId);
		BooleanExpression afterCursor = buildLikeCountCursor(cursorId, cursorLikeCount);

		return jpaQueryFactory
			.selectFrom(c)
			.leftJoin(c.user).fetchJoin()
			.leftJoin(c.article).fetchJoin()
			.where(byArticle, afterCursor)
			.orderBy(c.likeCount.desc(), c.id.desc())
			.limit(limit + 1)
			.fetch();
	}

	private BooleanExpression articleIdEq(Long articleId) {
		return articleId != null ? c.article.id.eq(articleId) : null;
	}

	private BooleanExpression buildCreatedAtCursor(Long cursorId, LocalDateTime cursorCreatedAt) {
		if (cursorId == null || cursorCreatedAt == null) return null;

		return c.createdAt.lt(cursorCreatedAt)
			.or(c.createdAt.eq(cursorCreatedAt).and(c.id.lt(cursorId)));
	}

	private BooleanExpression buildLikeCountCursor(Long cursorId, Integer cursorLikeCount) {
		if (cursorId == null || cursorLikeCount == null) return null;

		return c.likeCount.lt(cursorLikeCount)
			.or(c.likeCount.eq(cursorLikeCount).and(c.id.lt(cursorId)));
	}

}
