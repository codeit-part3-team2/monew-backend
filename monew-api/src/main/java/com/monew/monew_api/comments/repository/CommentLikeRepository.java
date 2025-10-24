package com.monew.monew_api.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monew.monew_api.comments.entity.CommentLike;

public interface CommentLikeRepository extends JpaRepository<CommentLike,Long> {

	boolean existsByCommentIdAndUserId(Long commentId, Long userId);

	void deleteByCommentIdAndUserId(Long commentId, Long userId);

}
