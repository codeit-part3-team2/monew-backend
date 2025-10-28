package com.monew.monew_api.comments.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monew.monew_api.comments.event.CommentCreatedEvent;
import com.monew.monew_api.comments.event.CommentLikedEvent;
import com.monew.monew_api.comments.dto.CommentResponseDto;
import com.monew.monew_api.comments.entity.Comment;
import com.monew.monew_api.comments.entity.CommentLike;
import com.monew.monew_api.comments.repository.CommentLikeRepository;
import com.monew.monew_api.comments.repository.CommentRepository;
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
	private final ApplicationEventPublisher eventPublisher;
	private final UserRepository userRepository;

	private static final String SORT_CREATED = "created";
	private static final String SORT_LIKE    = "like";

	@Transactional
	public Long write(Long userId, Long articleId, String content) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));
		Comment comment = Comment.of(user, articleId, content);
		Comment saved = commentRepository.save(comment);
		log.info("[COMMENT][CREATE] userId={}, articleId={}, commentId={}", userId, articleId, saved.getId());

		// 댓글 생성 시 이벤트 발행 (알림/활동로그)
		eventPublisher.publishEvent(new CommentCreatedEvent(saved.getId(), userId, articleId, saved.getCreatedAt()));

		return saved.getId();
	}

	@Transactional
	public void update(Long userId, Long commentId, String content) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
		validateOwner(userId, comment);
		log.info("[COMMENT][UPDATE] userId={}, commentId={}, contentLength={}", userId, commentId, content.length());
		comment.updateContent(content);
	}

	private void validateOwner(Long userId, Comment comment) {
		if (!comment.isOwnedBy(userId)) {
			throw new IllegalStateException("작성자만 수행할 수 있습니다.");
		}
	}

	@Transactional
	public void delete(Long userId, Long commentId) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
		validateOwner(userId, comment);
		log.info("[COMMENT][DELETE] userId={}, commentId={}", userId, commentId);
		commentRepository.delete(comment);
	}

	@Transactional
	public void like(Long userId, Long commentId) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

		try {
			commentLikeRepository.save(CommentLike.of(user, commentId));
			comment.increaseLike();

			log.info("[COMMENT][LIKE] userId={}, commentId={}", userId, commentId);
			eventPublisher.publishEvent(
				new CommentLikedEvent(comment.getId(), comment.getUser().getId(), userId, LocalDateTime.now())
			);
		} catch (DataIntegrityViolationException e) {
			return;
		}
	}

	@Transactional
	public void unlike(Long userId, Long commentId) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
		boolean liked = commentLikeRepository.existsByCommentIdAndUser_Id(commentId, userId);
		if (!liked) {
			return;
		}
		commentLikeRepository.deleteByCommentIdAndUser_Id(commentId, userId);
		log.info("[COMMENT][UNLIKE] userId={}, commentId={}", userId, commentId);
		comment.decreaseLike();
	}

	public List<CommentResponseDto> getCommentsByArticleIdWithCursor(
		Long articleId,
		int limit,
		String sort,
		Long cursorId,
		LocalDateTime cursorCreatedAt,
		Integer cursorLikeCount
	) {
		String s = (sort == null) ? SORT_CREATED : sort.toLowerCase();

		List<Comment> page;
		if (SORT_LIKE.equals(s)) {
			page = commentRepository.findPageByArticleIdOrderByLikeCountDesc(
				articleId, cursorId, cursorLikeCount, limit
			);
		} else {
			page = commentRepository.findPageByArticleIdOrderByCreatedAtDesc(
				articleId, cursorId, cursorCreatedAt, limit
			);
		}

		return page.stream().map(CommentResponseDto::from).toList();
	}
}
