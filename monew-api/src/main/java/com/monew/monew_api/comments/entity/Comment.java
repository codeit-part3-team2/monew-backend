package com.monew.monew_api.comments.entity;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.monew.monew_api.common.entity.BaseTimeEntity;
import com.monew.monew_api.domain.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
	name = "comments",
	indexes = {
		@Index(name = "ix_comments_user", columnList = "user_id"),
		@Index(name = "ix_comments_article", columnList = "article_id")
	}
)
@SQLDelete(sql = "UPDATE comments SET is_deleted = true, updated_at = now() WHERE id = ?")
@Where(clause = "is_deleted = false")
public class Comment extends BaseTimeEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "article_id", nullable = false)
	// private Article article;

	@Column(name = "article_id", nullable = false)
	private Long articleId;

	@Size(max = 500)
	@NotBlank
	@Column(name = "content", nullable = false, length = 500)
	private String content;

	@Column(name = "is_deleted", nullable = false)
	private boolean deleted = false;

	@Column(name = "like_count", nullable = false)
	private int likeCount = 0;

	private Comment(User user, Long articleId, String content) {
		this.user = user;
		this.articleId = articleId;
		this.content  = content;
	}

	public static Comment of(User user, Long articleId, String content) {
		return new Comment(user, articleId, content);
	}

	public boolean isOwnedBy(Long userId) { return this.user != null && this.user.getId().equals(userId); }
	public void updateContent(String content) { this.content = content; }

	public void increaseLike() { this.likeCount += 1; }
	public void decreaseLike() {
		if (this.likeCount > 0) this.likeCount -= 1;
	}
	public Long getUserId() {
		return this.user != null ? this.user.getId() : null;
	}
	public String getUserIdAsString() {
		return String.valueOf(getUserId());
	}
}
