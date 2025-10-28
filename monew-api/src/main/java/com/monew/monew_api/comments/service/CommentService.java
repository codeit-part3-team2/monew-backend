package com.monew.monew_api.comments.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monew.monew_api.article.entity.Article;
import com.monew.monew_api.article.repository.ArticleRepository;
import com.monew.monew_api.comments.dto.CommentDto;
import com.monew.monew_api.comments.dto.CommentLikeDto;
import com.monew.monew_api.comments.dto.CommentRegisterRequest;
import com.monew.monew_api.comments.dto.CommentUpdateRequest;
import com.monew.monew_api.comments.dto.CursorPageResponseCommentDto;
import com.monew.monew_api.comments.entity.Comment;
import com.monew.monew_api.comments.entity.CommentLike;
import com.monew.monew_api.comments.event.CommentCreatedEvent;
import com.monew.monew_api.comments.event.CommentLikedEvent;
import com.monew.monew_api.comments.repository.CommentLikeRepository;
import com.monew.monew_api.comments.repository.CommentRepository;
import com.monew.monew_api.common.exception.comment.CommentAlreadyLikedException;
import com.monew.monew_api.common.exception.comment.CommentArticleNotFoundException;
import com.monew.monew_api.common.exception.comment.CommentForbiddenException;
import com.monew.monew_api.common.exception.comment.CommentNotFoundException;
import com.monew.monew_api.common.exception.comment.CommentNotLikedException;
import com.monew.monew_api.common.exception.comment.CommentUserNotFoundException;
import com.monew.monew_api.domain.user.User;
import com.monew.monew_api.domain.user.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

	private static final ZoneId KST = ZoneId.of("Asia/Seoul");

	private final CommentRepository commentRepository;
	private final CommentLikeRepository commentLikeRepository;
	private final UserRepository userRepository;
	private final ArticleRepository articleRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public CommentDto register(CommentRegisterRequest request) {
		User user = getUserById(request.getUserIdAsLong());
		Article article = getArticleById(request.getArticleIdAsLong());

		Comment saved = commentRepository.save(Comment.of(user, article, request.content()));
		log.info("[COMMENT][CREATE] userId={}, articleId={}, commentId={}",
			user.getId(), article.getId(), saved.getId());

		eventPublisher.publishEvent(
			new CommentCreatedEvent(saved.getId(), user.getId(), article.getId(), saved.getCreatedAt())
		);

		return CommentDto.from(saved, false);
	}

	@Transactional
	public CommentDto update(Long userId, Long commentId, CommentUpdateRequest request) {
		Comment comment = getCommentById(commentId);
		validateOwnership(comment, userId);

		comment.updateContent(request.content());
		log.info("[COMMENT][UPDATE] userId={}, commentId={}, contentLength={}",
			userId, commentId, request.content().length());

		boolean likedByMe = commentLikeRepository.existsByComment_IdAndUser_Id(commentId, userId);
		return CommentDto.from(comment, likedByMe);
	}

	@Transactional
	public void delete(Long userId, Long commentId) {
		Comment comment = getCommentById(commentId);
		validateOwnership(comment, userId);

		commentRepository.delete(comment);
		log.info("[COMMENT][DELETE] userId={}, commentId={}", userId, commentId);
	}

	@Transactional
	public CommentLikeDto like(Long userId, Long commentId) {
		User user = getUserById(userId);
		Comment comment = getCommentById(commentId);

		CommentLike saved;
		try {
			saved = commentLikeRepository.save(CommentLike.of(user, comment));
		} catch (DataIntegrityViolationException e) {
			throw new CommentAlreadyLikedException();
		}

		comment.increaseLike();

		eventPublisher.publishEvent(
			new CommentLikedEvent(comment.getId(), comment.getUserId(), userId, LocalDateTime.now())
		);

		return CommentLikeDto.from(saved);
	}

	@Transactional
	public void dislike(Long userId, Long commentId) {  // void 반환
		boolean liked = commentLikeRepository.existsByComment_IdAndUser_Id(commentId, userId);
		if (!liked) throw new CommentNotLikedException();

		commentLikeRepository.deleteByComment_IdAndUser_Id(commentId, userId);
		commentRepository.decLikeCount(commentId);

		log.info("[COMMENT][DISLIKE] userId={}, commentId={}", userId, commentId);
	}

	public CursorPageResponseCommentDto findAll(
		Long articleId,
		int size,
		String orderBy,
		Long cursorId,
		LocalDateTime cursorCreatedAt,
		Integer cursorLikeCount,
		Long requestUserId
	) {
		final boolean orderByLike = "likeCount".equalsIgnoreCase(orderBy);

		List<Comment> page = orderByLike
			? commentRepository.findPageByArticleIdOrderByLikeCountDesc(articleId, cursorId, cursorLikeCount, size)
			: commentRepository.findPageByArticleIdOrderByCreatedAtDesc(articleId, cursorId, cursorCreatedAt, size);

		boolean hasNext = page.size() > size;
		if (hasNext)
			page = page.subList(0, size);

		Set<Long> likedCommentIds = requestUserId == null || page.isEmpty()
			? Set.of()
			: commentLikeRepository
			.findByUser_IdAndComment_IdIn(requestUserId,
				page.stream().map(Comment::getId).toList())
			.stream()
			.map(cl -> cl.getComment().getId())
			.collect(Collectors.toSet());

		List<CommentDto> content = page.stream()
			.map(c -> CommentDto.from(c, likedCommentIds.contains(c.getId())))
			.toList();

		String nextCursor = null;
		if (hasNext) {
			Comment last = page.get(page.size() - 1);
			if (orderByLike) {
				nextCursor = last.getLikeCount() + ":" + last.getId();
			} else {
				nextCursor = String.valueOf(last.getId());
			}
		}

		ZonedDateTime nextAfter = null;
		if (hasNext) {
			LocalDateTime lastCreated = page.get(page.size() - 1).getCreatedAt();
			nextAfter = lastCreated.atZone(KST);
		}

		return new CursorPageResponseCommentDto(
			content,
			nextCursor,
			nextAfter,
			content.size(),
			-1L,
			hasNext
		);
	}

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public void hardDelete(Long commentId) {
		if (!commentRepository.existsById(commentId)) {
			throw new CommentNotFoundException();
		}
		commentRepository.hardDeleteById(commentId);
		log.info("[COMMENT][HARD_DELETE] commentId={}", commentId);
	}

	public CommentDto findById(Long commentId, String userId) {
		Comment comment = getCommentById(commentId);
		boolean likedByMe = false;
		if (userId != null && !userId.isBlank()) {
			likedByMe = commentLikeRepository.existsByComment_IdAndUser_Id(commentId, Long.parseLong(userId));
		}
		return CommentDto.from(comment, likedByMe);
	}

	public CommentLikeDto findLike(Long commentId, Long userId) {
		return CommentLikeDto.of(commentId, userId);
	}

	// === 내부 유틸 ===

	private void validateOwnership(Comment comment, Long userId) {
		if (!comment.isOwnedBy(userId))
			throw new CommentForbiddenException();
	}

	private Comment getCommentById(Long commentId) {
		return commentRepository.findById(commentId).orElseThrow(CommentNotFoundException::new);
	}

	private User getUserById(Long userId) {
		return userRepository.findById(userId).orElseThrow(CommentUserNotFoundException::new);
	}

	private Article getArticleById(Long articleId) {
		return articleRepository.findById(articleId).orElseThrow(CommentArticleNotFoundException::new);
	}
}
