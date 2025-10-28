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
import com.monew.monew_api.common.exception.comment.CommentInvalidArticleIdException;
import com.monew.monew_api.common.exception.comment.CommentInvalidUserIdException;
import com.monew.monew_api.common.exception.comment.CommentNotFoundException;

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
		@RequestHeader("Monew-Request-User-ID") String userIdHeader,
		@RequestParam(required = false) String articleId,
		@RequestParam String orderBy,
		@RequestParam String direction,
		@RequestParam(required = false) String cursor,
		@RequestParam(required = false) String after,
		@RequestParam int limit
	) {
		Long aid = parseNullableArticleId(articleId);
		Long uid = parseUserId(userIdHeader);

		Long cursorId = null;
		LocalDateTime cursorCreatedAt = parseNullableDateTime(after);
		Integer cursorLikeCount = null;

		if (cursor != null && !cursor.isBlank()) {
			if ("likeCount".equalsIgnoreCase(orderBy)) {
				String[] parts = cursor.split(":");
				if (parts.length == 2) {
					cursorLikeCount = safeParseInt(parts[0]);
					cursorId = safeParseLong(parts[1]);
				} else {

				}
			} else {
				cursorId = safeParseLong(cursor);
			}
		}

		CursorPageResponseCommentDto page =
			commentService.findAll(aid, limit, orderBy, cursorId, cursorCreatedAt, cursorLikeCount, uid);

		return ResponseEntity.ok(page);
	}

	@PostMapping
	public ResponseEntity<CommentDto> register(
		@RequestHeader("Monew-Request-User-ID") String userIdHeader,
		@Valid @RequestBody CommentRegisterRequest request
	) {
		CommentRegisterRequest fixed = request.withUserId(userIdHeader);
		CommentDto dto = commentService.register(fixed);
		return ResponseEntity.status(HttpStatus.CREATED).body(dto);
	}

	@PatchMapping("/{commentId}")
	public ResponseEntity<CommentDto> update(
		@RequestHeader("Monew-Request-User-ID") String userIdHeader,
		@PathVariable String commentId,
		@Valid @RequestBody CommentUpdateRequest request
	) {
		Long userId = parseUserId(userIdHeader);
		Long cid = parseCommentId(commentId);
		CommentDto dto = commentService.update(userId, cid, request);
		return ResponseEntity.ok(dto);
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> delete(
		@RequestHeader("Monew-Request-User-ID") String userIdHeader,
		@PathVariable String commentId
	) {
		Long userId = parseUserId(userIdHeader);
		Long cid = parseCommentId(commentId);
		commentService.delete(userId, cid);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{commentId}/comment-likes")
	public ResponseEntity<CommentLikeDto> like(
		@RequestHeader("Monew-Request-User-ID") String userIdHeader,
		@PathVariable String commentId
	) {
		Long userId = parseUserId(userIdHeader);
		Long cid = parseCommentId(commentId);
		CommentLikeDto dto = commentService.like(userId, cid);
		return ResponseEntity.ok(dto);
	}

	@DeleteMapping("/{commentId}/comment-likes")
	public ResponseEntity<Void> dislike(
		@RequestHeader("Monew-Request-User-ID") String userIdHeader,
		@PathVariable String commentId
	) {
		Long userId = parseUserId(userIdHeader);
		Long cid = parseCommentId(commentId);
		commentService.dislike(userId, cid);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/{commentId}/hard")
	public ResponseEntity<Void> hardDelete(@PathVariable String commentId) {
		Long cid = parseCommentId(commentId);
		commentService.hardDelete(cid);
		return ResponseEntity.noContent().build();
	}

	private Long parseUserId(String userId) {
		try {
			return Long.parseLong(userId);
		} catch (Exception e) {
			throw new CommentInvalidUserIdException(userId);
		}
	}

	private Long parseCommentId(String commentId) {
		try {
			return Long.parseLong(commentId);
		} catch (Exception e) {
			throw new CommentNotFoundException();
		}
	}

	private Long parseNullableArticleId(String articleId) {
		if (articleId == null || articleId.isBlank()) return null;
		try {
			return Long.parseLong(articleId);
		} catch (Exception e) {
			throw new CommentInvalidArticleIdException(articleId);
		}
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
