package com.monew.monew_api.comments.entity;

import com.monew.monew_api.common.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
	name = "coment_likes",
	uniqueConstraints = {
		@UniqueConstraint(name = "uq_comment_likes", columnNames = {"user_id", "comment_id"})
	},
	indexes = {
		@Index(name = "ix_comment_likes_user", columnList = "user_id"),
		@Index(name = "ix_comment_likes_comment", columnList = "comment_id")
	}

)

public class CommentLike extends BaseTimeEntity {

	// @ManyToOne(fetch = LAZY) @JoinColumn(name="user_id", nullable=false)
	// private User user;

	// @ManyToOne(fetch = LAZY) @JoinColumn(name="comment_id", nullable=false)
	// private Comment comment;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "comment_id", nullable = false)
	private Long commentId;

	private CommentLike(Long userId, Long commentId) {
		this.userId = userId;
		this.commentId = commentId;
	}

	public static CommentLike of(Long userId, Long commentId) {
		return new CommentLike(userId, commentId);
	}

}
