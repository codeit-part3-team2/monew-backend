package com.monew.monew_api.comments.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentSearchRequest {

	private Long articleId;

	@Pattern(regexp = "createdAt|likeCount", message = "orderBy는 'createdAt' 또는 'likeCount'만 가능합니다.")
	private String orderBy = "createdAt";

	private String direction = "DESC";

	private String cursor;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime after;

	@Min(value = 1, message = "limit은 1 이상이어야 합니다.")
	@Max(value = 50, message = "limit은 최대 50까지만 가능합니다.")
	private int limit = 10;

}
