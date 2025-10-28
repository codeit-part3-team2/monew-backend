package com.monew.monew_api.comments.entity;

import com.monew.monew_api.common.entity.BaseCreatedEntity;
import com.monew.monew_api.domain.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
	name = "comment_likes",
	uniqueConstraints = {
		@UniqueConstraint(name = "uq_comment_likes", columnNames = {"user_id", "comment_id"})
	},
	indexes = {
		@Index(name = "ix_comment_likes_user", columnList = "user_id"),
		@Index(name = "ix_comment_likes_comment", columnList = "comment_id")
	}

)
public class CommentLike extends BaseCreatedEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id", nullable=false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="comment_id", nullable=false)
	private Comment comment;

	private CommentLike(User user, Comment comment) {
		this.user = user;
		this.comment = comment;
	}

	public static CommentLike of(User user, Comment comment) {
		return new CommentLike(user, comment);
	}

	public boolean isByUser(Long userId) {
		return user != null && user.getId().equals(userId);
	}

	public boolean isForComment(Long commentId) {
		return comment != null && comment.getId().equals(commentId);
	}
}
