package com.monew.monew_api.comments.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.monew.monew_api.comments.dto.CommentDto;
import com.monew.monew_api.comments.dto.CommentLikeDto;
import com.monew.monew_api.comments.dto.CommentRegisterRequest;
import com.monew.monew_api.comments.dto.CommentSearchRequest;
import com.monew.monew_api.comments.dto.CommentUpdateRequest;
import com.monew.monew_api.comments.dto.CursorPageResponseCommentDto;
import com.monew.monew_api.comments.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {

	private static final String REQUEST_HEADER_USER_ID = "MoNew-Request-User-ID";
	private final CommentService commentService;

	// 댓글 조회
	@GetMapping
	public ResponseEntity<CursorPageResponseCommentDto> findAll(
		@RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
		@ModelAttribute CommentSearchRequest request
	) {
		log.info("[CommentController] GET /api/comments - userId={}, request={}", userId, request);
		CursorPageResponseCommentDto response = commentService.findAll(userId, request);
		return ResponseEntity.ok(response);
	}

	// 댓글 작성
	@PostMapping
	public ResponseEntity<CommentDto> register(
		@Valid @RequestBody CommentRegisterRequest request
	) {
		log.info("[CommentController] POST /api/comments - register request={}", request);
		CommentDto dto = commentService.register(request);
		log.info("[CommentController] POST /api/comments - created commentId={}", dto.id());
		return ResponseEntity.status(HttpStatus.CREATED).body(dto);
	}

	// 댓글 수정
	@PatchMapping("/{commentId}")
	public ResponseEntity<CommentDto> update(
		@RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
		@PathVariable Long commentId,
		@Valid @RequestBody CommentUpdateRequest request
	) {
		log.info("[CommentController] PATCH /api/comments/{} - userId={}, request={}", commentId, userId, request);
		CommentDto dto = commentService.update(userId, commentId, request);
		log.info("[CommentController] PATCH /api/comments/{} - updated", commentId);
		return ResponseEntity.ok(dto);
	}

	// 댓글 좋아요
	@PostMapping("/{commentId}/comment-likes")
	public ResponseEntity<CommentLikeDto> like(
		@RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
		@PathVariable Long commentId
	) {
		log.info("[CommentController] POST /api/comments/{}/comment-likes - like request, userId={}"
			, commentId, userId);
		CommentLikeDto dto = commentService.like(userId, commentId);
		log.info("[CommentController] POST /api/comments/{}/comment-likes - like success, likeId={}"
			, commentId, dto.id());
		return ResponseEntity.ok(dto);
	}

	// 댓글 좋아요 삭제
	@DeleteMapping("/{commentId}/comment-likes")
	public ResponseEntity<Void> dislike(
		@RequestHeader(REQUEST_HEADER_USER_ID) Long userId,
		@PathVariable Long commentId
	) {
		log.info("[CommentController] DELETE /api/comments/{}/comment-likes - dislike request, userId={}"
			, commentId, userId);
		commentService.dislike(userId, commentId);
		log.info("[CommentController] DELETE /api/comments/{}/comment-likes - dislike success"
			, commentId);
		return ResponseEntity.noContent().build();
	}

	// 댓글 논리 삭제
	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> delete(
		@PathVariable Long commentId
	) {
		log.info("[CommentController] DELETE /api/comments/{} - soft delete request", commentId);
		commentService.delete(commentId);
		log.info("[CommentController] DELETE /api/comments/{} - soft delete success", commentId);
		return ResponseEntity.noContent().build();
	}

	// 댓글 물리 삭제
	@DeleteMapping("/{commentId}/hard")
	public ResponseEntity<Void> hardDelete(
		@PathVariable Long commentId) {
		log.info("[CommentController] DELETE /api/comments/{}/hard - hard delete request", commentId);
		commentService.hardDelete(commentId);
		log.info("[CommentController] DELETE /api/comments/{}/hard - hard delete success", commentId);
		return ResponseEntity.noContent().build();
	}
}
