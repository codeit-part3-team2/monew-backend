package com.monew.monew_api.comments.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.monew.monew_api.comments.dto.*;
import com.monew.monew_api.comments.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

	private final CommentService commentService;

	@PostMapping
	public ResponseEntity<CommentDto> register(
		@RequestHeader("Monew-Request-User-ID") String userId,
		@Valid @RequestBody CommentRegisterRequest request
	) {
		Long savedId = commentService.register(request.withUserId(userId));
		CommentDto response = commentService.findById(savedId, userId);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PatchMapping("/{commentId}")
	public ResponseEntity<CommentDto> update(
		@RequestHeader("Monew-Request-User-ID") String userId,
		@PathVariable String commentId,
		@Valid @RequestBody CommentUpdateRequest request
	) {
		commentService.update(Long.parseLong(userId), Long.parseLong(commentId), request);
		CommentDto updated = commentService.findById(Long.parseLong(commentId), userId);
		return ResponseEntity.ok(updated);
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> delete(
		@RequestHeader("Monew-Request-User-ID") String userId,
		@PathVariable String commentId
	) {
		commentService.delete(Long.parseLong(userId), Long.parseLong(commentId));
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{commentId}/comment-likes")
	public ResponseEntity<CommentLikeDto> like(
		@RequestHeader("Monew-Request-User-ID") String userId,
		@PathVariable String commentId
	) {
		commentService.like(Long.parseLong(userId), Long.parseLong(commentId));
		CommentLikeDto response = commentService.findLike(Long.parseLong(commentId), Long.parseLong(userId));
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{commentId}/comment-likes")
	public ResponseEntity<Void> disLike(
		@RequestHeader("Monew-Request-User-ID") String userId,
		@PathVariable String commentId
	) {
		commentService.disLike(Long.parseLong(userId), Long.parseLong(commentId));
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<List<CommentDto>> findAll(
		@RequestHeader("Monew-Request-User-ID") String userId,
		@RequestParam String articleId,
		@RequestParam String orderBy,
		@RequestParam String direction,
		@RequestParam(required = false) String cursor,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime after,
		@RequestParam int limit
	) {
		List<CommentDto> comments = commentService.findAll(
			Long.parseLong(articleId),
			limit,
			orderBy,
			direction,
			cursor != null ? Long.parseLong(cursor) : null,
			after,
			Long.parseLong(userId)
		);

		return ResponseEntity.ok(comments);
	}
}
