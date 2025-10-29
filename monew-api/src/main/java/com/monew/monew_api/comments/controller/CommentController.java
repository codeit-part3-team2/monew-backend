package com.monew.monew_api.comments.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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

import com.monew.monew_api.comments.dto.CommentDto;
import com.monew.monew_api.comments.dto.CommentLikeDto;
import com.monew.monew_api.comments.dto.CommentRegisterRequest;
import com.monew.monew_api.comments.dto.CommentUpdateRequest;
import com.monew.monew_api.comments.dto.CursorPageResponseCommentDto;
import com.monew.monew_api.comments.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {

	private final CommentService commentService;

	@GetMapping
	public ResponseEntity<CursorPageResponseCommentDto> findAll(
		@RequestHeader("Monew-Request-User-ID") Long userIdHeader,
		@RequestParam(required = false) Long articleId,
		@RequestParam String orderBy,
		@RequestParam(required = false) String direction,
		@RequestParam(required = false) String cursor,
		@RequestParam(required = false) String after,
		@RequestParam int limit
	) {
		Long cursorId = null;
		LocalDateTime cursorCreatedAt = parseNullableDateTime(after);
		Integer cursorLikeCount = null;

		if (cursor != null && !cursor.isBlank()) {
			if ("likeCount".equalsIgnoreCase(orderBy)) {
				String[] parts = cursor.split(":");
				if (parts.length == 2) {
					cursorLikeCount = safeParseInt(parts[0]);
					cursorId = safeParseLong(parts[1]);
				}
			} else {
				cursorId = safeParseLong(cursor);
			}
		}

		CursorPageResponseCommentDto page =
			commentService.findAll(articleId, limit, orderBy, cursorId, cursorCreatedAt, cursorLikeCount, userIdHeader);

		return ResponseEntity.ok(page);
	}

	@PostMapping
	public ResponseEntity<CommentDto> register(
		@Valid @RequestBody CommentRegisterRequest request
	) {
		CommentDto dto = commentService.register(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(dto);
	}

	@PatchMapping("/{commentId}")
	public ResponseEntity<CommentDto> update(
		@RequestHeader("Monew-Request-User-ID") Long userIdHeader,
		@PathVariable Long commentId,
		@Valid @RequestBody CommentUpdateRequest request
	) {
		CommentDto dto = commentService.update(userIdHeader, commentId, request);
		return ResponseEntity.ok(dto);
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> delete(
		@PathVariable Long commentId
	) {
		commentService.delete(commentId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{commentId}/comment-likes")
	public ResponseEntity<CommentLikeDto> like(
		@RequestHeader("Monew-Request-User-ID") Long userIdHeader,
		@PathVariable Long commentId
	) {
		CommentLikeDto dto = commentService.like(userIdHeader, commentId);
		return ResponseEntity.ok(dto);
	}

	@DeleteMapping("/{commentId}/comment-likes")
	public ResponseEntity<Void> dislike(
		@RequestHeader("Monew-Request-User-ID") Long userIdHeader,
		@PathVariable Long commentId
	) {
		commentService.dislike(userIdHeader, commentId);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{commentId}/hard")
	public ResponseEntity<Void> hardDelete(@PathVariable Long commentId) {
		commentService.hardDelete(commentId);
		return ResponseEntity.noContent().build();
	}

	private LocalDateTime parseNullableDateTime(String text) {
		return (text == null || text.isBlank()) ? null : LocalDateTime.parse(text);
	}

	private Long safeParseLong(String s) {
		try { return Long.parseLong(s); } catch (Exception e) { return null; }
	}

	private Integer safeParseInt(String s) {
		try { return Integer.parseInt(s); } catch (Exception e) { return null; }
	}
}
