package com.monew.monew_api.comments.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.monew.monew_api.comments.dto.CommentRequestDto;
import com.monew.monew_api.comments.dto.CommentResponseDto;
import com.monew.monew_api.comments.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

	private final CommentService commentService;

	@PostMapping("/comments")
	public ResponseEntity<String> writeComment(
		@RequestHeader("MoNew-Request-User-ID") Long userId,
		@Valid @RequestBody CommentRequestDto request
	) {
		Long savedId = commentService.write(
			userId,
			request.getArticleIdAsLong(),
			request.getContent()
		);
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(String.valueOf(savedId));
	}

	@GetMapping("/articles/{articleId}/comments")
	public ResponseEntity<List<CommentResponseDto>> getCommentsByArticle(
		@PathVariable Long articleId,
		@RequestParam(required = false, defaultValue = "10") Integer limit,
		@RequestParam(required = false, defaultValue = "created") String sort,
		@RequestParam(required = false) Long cursorId,
		@RequestParam(required = false)
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursorCreatedAt,
		@RequestParam(required = false) Integer cursorLikeCount
	) {

		List<CommentResponseDto> comments = commentService.getCommentsByArticleIdWithCursor(
			articleId, limit, sort, cursorId, cursorCreatedAt, cursorLikeCount
		);
		return ResponseEntity.ok(comments);
	}

	@PatchMapping("/comments/{commentId}")
	public ResponseEntity<Void> updateComment(
		@RequestHeader("MoNew-Request-User-ID") Long userId,
		@PathVariable Long commentId,
		@Valid @RequestBody CommentRequestDto request
	) {
		commentService.update(userId, commentId, request.getContent());
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/comments/{commentId}")
	public ResponseEntity<Void> deleteComment(
		@RequestHeader("MoNew-Request-User-ID") Long userId,
		@PathVariable Long commentId
	) {
		commentService.delete(userId, commentId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/comments/{commentId}/like")
	public ResponseEntity<Void> likeComment(
		@RequestHeader("MoNew-Request-User-ID") Long userId,
		@PathVariable Long commentId
	) {
		commentService.like(userId, commentId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/comments/{commentId}/like")
	public ResponseEntity<Void> unlikeComment(
		@RequestHeader("MoNew-Request-User-ID") Long userId,
		@PathVariable Long commentId
	) {
		commentService.unlike(userId, commentId);
		return ResponseEntity.noContent().build();
	}
}
