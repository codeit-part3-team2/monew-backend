package com.monew.monew_api.comments.repository.impl;

import static com.monew.monew_api.comments.entity.QComment.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.monew.monew_api.comments.dto.CommentDto;
import com.monew.monew_api.comments.dto.CursorPageResponseCommentDto;
import com.monew.monew_api.comments.dto.QCommentDto;
import com.monew.monew_api.comments.entity.QComment;
import com.monew.monew_api.comments.entity.QCommentLike;
import com.monew.monew_api.comments.repository.CommentRepositoryCustom;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

	private static final ZoneId KST = ZoneId.of("Asia/Seoul");
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public CursorPageResponseCommentDto searchComments(
		Long articleId, String orderBy, String cursor,
		LocalDateTime after, int limit, Long userId) {

		QCommentLike cl = QCommentLike.commentLike;

		// DTO로 바로 조회 (서브쿼리로 좋아요 여부 포함)
		List<CommentDto> comments = jpaQueryFactory
			.select(new QCommentDto(
				comment.id,
				comment.user.id,
				comment.article.id,
				comment.user.nickname,
				comment.content,
				comment.likeCount,
				JPAExpressions // 좋아요 여부 확인하는 서브쿼리
					.selectOne()
					.from(cl)
					.where(
						cl.comment.id.eq(comment.id)
							.and(cl.user.id.eq(userId))
					)
					.exists(),
				comment.createdAt.stringValue()
			))
			.from(comment)
			.where(
				articleIdEq(articleId),
				cursorCondition(orderBy, cursor, after)
			)
			.orderBy(orderSpecifiers(comment, orderBy))
			.limit(limit + 1L)
			.fetch();

		log.info("[조회 결과] 총 {}개 댓글 조회됨", comments.size());

		// hasNext 계산
		boolean hasNext = comments.size() > limit;
		if (hasNext) {
			comments.remove(limit);
		}

		// nextCursor, nextAfter 생성
		String nextCursor = null;
		ZonedDateTime nextAfter = null;

		if (hasNext && !comments.isEmpty()) {
			CommentDto last = comments.get(comments.size() - 1);

			if ("likeCount".equalsIgnoreCase(orderBy)) {
				nextCursor = last.likeCount() + ":" + last.id();
			} else {
				nextCursor = String.valueOf(last.id());
			}

			// after는 입력받은 값을 그대로 유지 (시간 필터 고정)
			nextAfter = after != null ? after.atZone(KST) : null;

		}

		return new CursorPageResponseCommentDto(
			comments,
			nextCursor,
			nextAfter,
			comments.size(),
			-1L,
			hasNext
		);
	}

	// 커서 조건 분배(최신순, 인기순)
	private BooleanExpression cursorCondition(String orderBy, String cursor, LocalDateTime after) {
		if ("likeCount".equalsIgnoreCase(orderBy)) {
			return buildLikeCountCursor(cursor, after);
		} else {
			return buildCreatedAtCursor(cursor, after);
		}
	}

	// createdAt 기준 커서 조건 (최신순)
	private BooleanExpression buildCreatedAtCursor(
		String cursor, LocalDateTime after
	) {
		Long cursorId = parseLongCursor(cursor);
		// 둘 다 없으면 조건 없음
		if (cursorId == null && after == null) {
			return null;
		}

		// cursor만 있을 때
		if (cursorId != null && after == null) {
			return comment.id.lt(cursorId);
		}

		// after만 있을 때
		if (cursorId == null) {
			return comment.createdAt.lt(after);
		}

		// 둘 다 있을 때
		return comment.createdAt.lt(after)
			.or(
				comment.createdAt.eq(after)
					.and(comment.id.lt(cursorId))
			);
	}

	// likeCount 기준 커서 조건
	private BooleanExpression buildLikeCountCursor(String cursor, LocalDateTime after) {

		if (cursor == null || cursor.isBlank()) {
			return null;
		}

		String[] parts = cursor.split(":");
		if (parts.length != 2) {
			return null;
		}

		try {
			Integer likeCount = Integer.parseInt(parts[0]);
			Long id = Long.parseLong(parts[1]);

			return comment.likeCount.lt(likeCount)
				.or(
					comment.likeCount.eq(likeCount)
						.and(comment.id.lt(id))
				);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	// Long 타입 커서 파싱
	private Long parseLongCursor(String cursor) {
		if (cursor == null || cursor.isBlank()) {
			return null;
		}
		try {
			return Long.parseLong(cursor);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	// 게시글 필터 조건
	private BooleanExpression articleIdEq(Long articleId) {
		return articleId == null ? null : comment.article.id.eq(articleId);
	}

	// 정렬 컬럼 선택
	private OrderSpecifier<?>[] orderSpecifiers(QComment c, String orderBy) {
		if ("likeCount".equalsIgnoreCase(orderBy)) {
			return new OrderSpecifier[] {c.likeCount.desc(), c.id.desc()};
		}
		return new OrderSpecifier[] {c.createdAt.desc(), c.id.desc()};
	}
}
