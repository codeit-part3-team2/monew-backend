package com.monew.monew_api.comments.service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monew.monew_api.article.entity.Article;
import com.monew.monew_api.article.repository.ArticleRepository;
import com.monew.monew_api.comments.dto.CommentDto;
import com.monew.monew_api.comments.dto.CommentLikeDto;
import com.monew.monew_api.comments.dto.CommentRegisterRequest;
import com.monew.monew_api.comments.dto.CommentSearchRequest;
import com.monew.monew_api.comments.dto.CommentUpdateRequest;
import com.monew.monew_api.comments.dto.CursorPageResponseCommentDto;
import com.monew.monew_api.comments.entity.Comment;
import com.monew.monew_api.comments.entity.CommentLike;
import com.monew.monew_api.comments.event.CommentCreatedEvent;
import com.monew.monew_api.comments.event.CommentLikedEvent;
import com.monew.monew_api.comments.repository.CommentLikeRepository;
import com.monew.monew_api.comments.repository.CommentRepository;
import com.monew.monew_api.common.exception.comment.CommentArticleNotFoundException;
import com.monew.monew_api.common.exception.comment.CommentForbiddenException;
import com.monew.monew_api.common.exception.comment.CommentNotFoundException;
import com.monew.monew_api.common.exception.comment.CommentNotLikedException;
import com.monew.monew_api.common.exception.comment.CommentUserNotFoundException;
import com.monew.monew_api.domain.user.User;
import com.monew.monew_api.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

	private final CommentRepository commentRepository;
	private final CommentLikeRepository commentLikeRepository;
	private final UserRepository userRepository;
	private final ArticleRepository articleRepository;
	private final ApplicationEventPublisher eventPublisher;

	// 댓글 작성
	@Transactional
	public CommentDto register(CommentRegisterRequest request) {
		log.info("[COMMENT][CREATE][START] userId={}, articleId={}", request.userId(), request.articleId());
		User user = getUserById(request.userId());
		Article article = getArticleById(request.articleId());

		Comment saved = commentRepository.save(Comment.of(user, article, request.content()));
		log.info("[COMMENT][CREATE] userId={}, articleId={}, commentId={}",
			user.getId(), article.getId(), saved.getId());

		eventPublisher.publishEvent(
			new CommentCreatedEvent(saved.getId(), user.getId(), article.getId(), saved.getCreatedAt())
		);

		return CommentDto.from(saved, false);
	}

	// 댓글 수정
	@Transactional
	public CommentDto update(Long userId, Long commentId, CommentUpdateRequest request) {
		log.info("[COMMENT][UPDATE][START] userId={}, commentId={}", userId, commentId);
		Comment comment = getCommentById(commentId);
		validateOwnership(comment, userId);

		comment.updateContent(request.content());
		log.info("[COMMENT][UPDATE] userId={}, commentId={}, contentLength={}",
			userId, commentId, request.content().length());

		boolean likedByMe = commentLikeRepository.existsByComment_IdAndUser_Id(commentId, userId);
		return CommentDto.from(comment, likedByMe);
	}

	// 댓글 좋아요
	@Transactional
	public CommentLikeDto like(Long userId, Long commentId) {
		log.info("[COMMENT][LIKE] 좋아요 요청 시작 - userId={}, commentId={}", userId, commentId);
		User user = getUserById(userId);
		Comment comment = getCommentById(commentId);
		log.info("[COMMENT][LIKE] 엔티티 조회 완료 - user={}, comment={}", user.getId(), comment.getId());
		CommentLike saved = commentLikeRepository.save(CommentLike.of(user, comment));
		comment.increaseLike();

		eventPublisher.publishEvent(
			new CommentLikedEvent(comment.getId(), comment.getUserId(), userId, LocalDateTime.now())
		);
		log.info("[COMMENT][LIKE] userId={}, commentId={}", userId, commentId);
		return CommentLikeDto.from(saved);
	}

	// 댓글 좋아요 삭제
	@Transactional
	public void dislike(Long userId, Long commentId) {
		log.info("[COMMENT][DISLIKE][START] userId={}, commentId={}", userId, commentId);
		boolean liked = commentLikeRepository.existsByComment_IdAndUser_Id(commentId, userId);
		if (!liked)
			throw new CommentNotLikedException();

		commentLikeRepository.deleteByComment_IdAndUser_Id(commentId, userId);
		commentRepository.decLikeCount(commentId);

		log.info("[COMMENT][DISLIKE] userId={}, commentId={}", userId, commentId);
	}

	// 댓글 논리 삭제
	@Transactional
	public void delete(Long commentId) {
		log.info("[COMMENT][DELETE][START] commentId={}", commentId);
		Comment comment = getCommentById(commentId);

		commentRepository.delete(comment);
		log.info("[COMMENT][DELETE] commentId={}", commentId);
	}

	// 댓글 물리 삭제
	@Transactional
	public void hardDelete(Long commentId) {
		log.info("[COMMENT][HARD_DELETE][START] commentId={}", commentId);
		int deletedCount = commentRepository.hardDeleteById(commentId);
		// 0 = 실패, 1 = 성공
		if (deletedCount == 0) {
			throw new CommentNotFoundException();
		}
		log.info("[COMMENT][HARD_DELETE] commentId={}, deletedCount={}", commentId, deletedCount);
	}

	// 댓글 전체 조회
	public CursorPageResponseCommentDto findAll(
		Long userId, CommentSearchRequest request
	) {
		log.info("[COMMENT][FIND_ALL][START] userId={}, articleId={}, orderBy={}, cursor={}, after={}, limit={}",
			userId,
			request.getArticleId(),
			request.getOrderBy(),
			request.getCursor(),
			request.getAfter(),
			request.getLimit()
		);

		return commentRepository.searchComments(
			request.getArticleId(),
			request.getOrderBy(),
			request.getCursor(),
			request.getAfter(),
			request.getLimit(),
			userId
		);
	}

	// === 내부 유틸 ===

	// 작성자 확인
	private void validateOwnership(Comment comment, Long userId) {
		if (!comment.isOwnedBy(userId))
			throw new CommentForbiddenException();
	}

	// commentId 확인
	private Comment getCommentById(Long commentId) {
		return commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
	}

	// userId 확인
	private User getUserById(Long userId) {
		return userRepository.findById(userId).orElseThrow(CommentUserNotFoundException::new);
	}

	// articleId 확인
	private Article getArticleById(Long articleId) {
		return articleRepository.findById(articleId).orElseThrow(CommentArticleNotFoundException::new);
	}
}
