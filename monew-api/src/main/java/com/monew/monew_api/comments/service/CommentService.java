package com.monew.monew_api.comments.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monew.monew_api.article.entity.Article;
import com.monew.monew_api.article.repository.ArticleRepository;
import com.monew.monew_api.comments.dto.*;
import com.monew.monew_api.comments.entity.*;
import com.monew.monew_api.comments.event.CommentCreatedEvent;
import com.monew.monew_api.comments.event.CommentLikedEvent;
import com.monew.monew_api.comments.repository.CommentLikeRepository;
import com.monew.monew_api.comments.repository.CommentRepository;
import com.monew.monew_api.domain.user.User;
import com.monew.monew_api.domain.user.repository.UserRepository;
import com.monew.monew_api.common.exception.comment.*;

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

	@Transactional
	public Long register(CommentRegisterRequest request) {
		User user = getUserById(request.getUserIdAsLong());
		Article article = getArticleById(request.getArticleIdAsLong());

		Comment comment = Comment.of(user, article, request.content());
		Comment saved = commentRepository.save(comment);

		log.info("[COMMENT][CREATE] userId={}, articleId={}, commentId={}",
			user.getId(), article.getId(), saved.getId());

		eventPublisher.publishEvent(
			new CommentCreatedEvent(saved.getId(), user.getId(), article.getId(), saved.getCreatedAt())
		);

		return saved.getId();
	}

	@Transactional
	public void update(Long userId, Long commentId, CommentUpdateRequest request) {
		Comment comment = getCommentById(commentId);
		validateOwnership(comment, userId);

		comment.updateContent(request.content());
		log.info("[COMMENT][UPDATE] userId={}, commentId={}, contentLength={}",
			userId, commentId, request.content().length());
	}

	@Transactional
	public void delete(Long userId, Long commentId) {
		Comment comment = getCommentById(commentId);
		validateOwnership(comment, userId);
		commentRepository.delete(comment);
		log.info("[COMMENT][DELETE] userId={}, commentId={}", userId, commentId);
	}

	@Transactional
	public void like(Long userId, Long commentId) {
		User user = getUserById(userId);
		Comment comment = getCommentById(commentId);

		try {
			commentLikeRepository.save(CommentLike.of(user, comment));
			comment.increaseLike();

			log.info("[COMMENT][LIKE] userId={}, commentId={}", userId, commentId);
			eventPublisher.publishEvent(
				new CommentLikedEvent(comment.getId(), comment.getUserId(), userId, LocalDateTime.now())
			);
		} catch (DataIntegrityViolationException e) {
			throw new CommentAlreadyLikedException();
		}
	}

	@Transactional
	public void disLike(Long userId, Long commentId) {
		boolean liked = commentLikeRepository.existsByComment_IdAndUser_Id(commentId, userId);
		if (!liked) throw new CommentNotLikedException();

		commentLikeRepository.deleteByComment_IdAndUser_Id(commentId, userId);
		Comment comment = getCommentById(commentId);
		comment.decreaseLike();

		log.info("[COMMENT][DISLIKE] userId={}, commentId={}", userId, commentId);
	}

	public List<CommentDto> findAll(
		Long articleId,
		int limit,
		String orderBy,
		String direction,
		Long cursor,
		LocalDateTime after,
		Long requestUserId
	) {
		List<Comment> page = orderBy.equalsIgnoreCase("like")
			? commentRepository.findPageByArticleIdOrderByLikeCountDesc(articleId, cursor, null, limit)
			: commentRepository.findPageByArticleIdOrderByCreatedAtDesc(articleId, cursor, after, limit);

		return page.stream()
			.map(comment -> {
				boolean likedByMe = commentLikeRepository.existsByComment_IdAndUser_Id(comment.getId(), requestUserId);
				return CommentDto.from(comment, likedByMe);
			})
			.toList();
	}

	public CommentDto findById(Long commentId, String userId) {
		Comment comment = getCommentById(commentId);
		boolean likedByMe = commentLikeRepository.existsByComment_IdAndUser_Id(commentId, Long.parseLong(userId));
		return CommentDto.from(comment, likedByMe);
	}

	public CommentLikeDto findLike(Long commentId, Long userId) {
		boolean liked = commentLikeRepository.existsByComment_IdAndUser_Id(commentId, userId);
		return CommentLikeDto.of(commentId, userId, liked);
	}

	private void validateOwnership(Comment comment, Long userId) {
		if (!comment.isOwnedBy(userId)) {
			throw new CommentForbiddenException();
		}
	}

	private Comment getCommentById(Long commentId) {
		return commentRepository.findById(commentId)
			.orElseThrow(CommentNotFoundException::new);
	}

	private User getUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(CommentUserNotFoundException::new);
	}

	private Article getArticleById(Long articleId) {
		return articleRepository.findById(articleId)
			.orElseThrow(CommentArticleNotFoundException::new);
	}
}
