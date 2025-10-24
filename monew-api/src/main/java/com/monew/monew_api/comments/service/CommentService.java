package com.monew.monew_api.comments.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monew.monew_api.comments.dto.CommentResponseDto;
import com.monew.monew_api.comments.entity.Comment;
import com.monew.monew_api.comments.entity.CommentLike;
import com.monew.monew_api.comments.repository.CommentLikeRepository;
import com.monew.monew_api.comments.repository.CommentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

	private final CommentRepository commentRepository;
	private final CommentLikeRepository commentLikeRepository;
	// private final ApplicationEventPublisher eventPublisher; // 알림/활동 로그용 이벤트 훅

	// 댓글 작성
	@Transactional
	public Long write(Long userId, Long articleId, String content) {
		Comment comment = Comment.of(userId, articleId, content);
		Comment saved = commentRepository.save(comment);

		// 댓글 생성 시 이벤트 발행 (알림/활동로그)
		// eventPublisher.publishEvent(new CommentCreatedEvent(saved.getId(), userId, articleId));

		return saved.getId();
	}

	// 댓글 조회
	public List<CommentResponseDto> getCommentsByArticleId(Long articleId) {
		List<Comment> comments = commentRepository.findByArticleIdOrderByCreatedAtDesc(articleId);

		return comments.stream()
			.map(CommentResponseDto::from)
			.toList();
	}

	// 댓글 수정 (작성자)
	@Transactional
	public void update(Long userId, Long commentId, String content) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
		validateOwner(userId, comment);
		comment.updateContent(content);
	}

	private void validateOwner(Long userId, Comment comment) {
		if (!comment.getUserId().equals(userId)) {
			throw new IllegalStateException("작성자만 수행할 수 있습니다.");
		}
	}

	// 댓글 삭제 (작성자, soft delete)
	@Transactional
	public void delete(Long userId, Long commentId) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
		validateOwner(userId, comment);
		commentRepository.delete(comment);
	}

	// 댓글 좋아요 (중복 방지 + 카운트 증가)
	@Transactional
	public void like(Long userId, Long commentId) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
		try{
		 commentLikeRepository.save(CommentLike.of(userId, commentId));
		 comment.increaseLike();
		} catch (DataIntegrityViolationException e) {
		    throw new IllegalArgumentException("이미 좋아요한 댓글입니다.");
		}
	}

	// 댓글 좋아요 취소 (좋아요 취소 + 카운트 감소)
	@Transactional
	public void unlike(Long userId, Long commentId) {
		Comment comment = commentRepository.findById(commentId)
			.orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
		boolean liked = commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
		if(!liked){
			return;
		}
		commentLikeRepository.deleteByCommentIdAndUserId(userId, commentId);
		comment.decreaseLike();
	}
}
