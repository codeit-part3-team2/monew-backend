package com.monew.monew_api.comments.repository.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.monew.monew_api.comments.entity.Comment;
import com.monew.monew_api.comments.entity.QComment;
import com.monew.monew_api.comments.repository.CommentRepositoryCustom;
import com.querydsl.core.BooleanBuilder;
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

		QComment c = QComment.comment;

		BooleanBuilder where = new BooleanBuilder();
		if (articleId != null) {
			where.and(c.article.id.eq(articleId));
		}

		BooleanExpression cursorExpr = buildCreatedAtCursor(c, cursorId, cursorCreatedAt);
		if (cursorExpr != null) {
			where.and(cursorExpr);
		}

		return jpaQueryFactory
			.selectFrom(c)
			.where(where)
			.orderBy(
				c.createdAt.desc(),
				c.id.desc()
			)
			.limit(limit + 1L)
			.fetch();
	}

	@Override
	public List<Comment> findPageByArticleIdOrderByLikeCountDesc(Long articleId, Long cursorId, Integer cursorLikeCount,
		int limit) {

		QComment c = QComment.comment;

		BooleanBuilder where = new BooleanBuilder();
		if (articleId != null) {
			where.and(c.article.id.eq(articleId));
		}

		BooleanExpression cursorExpr = buildLikeCountCursor(c, cursorId, cursorLikeCount);
		if (cursorExpr != null) {
			where.and(cursorExpr);
		}

		return jpaQueryFactory
			.selectFrom(c)
			.where(where)
			.orderBy(
				c.likeCount.desc(),
				c.id.desc()
			)
			.limit(limit + 1L)
			.fetch();

	}

	private BooleanExpression buildCreatedAtCursor(
		QComment c,
		Long cursorId,
		LocalDateTime cursorCreatedAt
	) {
		if (cursorId == null && cursorCreatedAt == null) {
			return null;
		}

		if (cursorId != null && cursorCreatedAt == null) {
			return c.id.lt(cursorId);
		}

		if (cursorId == null && cursorCreatedAt != null) {

			return c.createdAt.lt(cursorCreatedAt);
		}

		return c.createdAt.lt(cursorCreatedAt)
			.or(
				c.createdAt.eq(cursorCreatedAt)
					.and(c.id.lt(cursorId))
			);
	}

	private BooleanExpression buildLikeCountCursor(
		QComment c,
		Long cursorId,
		Integer cursorLikeCount
	) {
		if (cursorId == null && cursorLikeCount == null) {
			return null;
		}

		if (cursorLikeCount != null && cursorId == null) {
			return c.likeCount.lt(cursorLikeCount);
		}

		if (cursorLikeCount == null && cursorId != null) {
			return c.id.lt(cursorId);
		}

		return c.likeCount.lt(cursorLikeCount)
			.or(
				c.likeCount.eq(cursorLikeCount)
					.and(c.id.lt(cursorId))
			);
	}

}
