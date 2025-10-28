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
	public List<Comment> findCommentsByArticleIdWithCursor(Long articleId, Long cursorId, int limit, String sort) {
		OrderSpecifier<?> orderBy = "like".equalsIgnoreCase(sort) ? c.likeCount.desc() : c.createdAt.desc();
		BooleanExpression byArticle = articleIdEq(articleId);
		BooleanExpression ltCursor = cursorId != null ? c.id.lt(cursorId) : null;

		return jpaQueryFactory
			.selectFrom(c)
			.where(byArticle, ltCursor)
			.orderBy(orderBy, c.id.desc())
			.limit(limit)
			.fetch();
	}

	@Override
	public List<Comment> findPageByArticleIdOrderByCreatedAtDesc(Long articleId, Long cursorId,
		LocalDateTime cursorCreatedAt, int limit) {

		BooleanExpression byArticle = articleIdEq(articleId);
		BooleanExpression afterCursor = buildCreatedAtCursor(cursorId, cursorCreatedAt);

		return jpaQueryFactory
			.selectFrom(c)
			.where(byArticle, afterCursor)
			.orderBy(c.createdAt.desc(), c.id.desc())
			.limit(limit)
			.fetch();
	}

	@Override
	public List<Comment> findPageByArticleIdOrderByLikeCountDesc(Long articleId, Long cursorId, Integer cursorLikeCount,
		int limit) {
		BooleanExpression byArticle = articleIdEq(articleId);
		BooleanExpression afterCursor = buildLikeCountCursor(cursorId, cursorLikeCount);

		return jpaQueryFactory
			.selectFrom(c)
			.where(byArticle, afterCursor)
			.orderBy(c.likeCount.desc(), c.id.desc())
			.limit(limit)
			.fetch();
	}

	// ✅ article.id 기반 비교로 변경
	private BooleanExpression articleIdEq(Long articleId) {
		return c.article.id.eq(articleId);
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
