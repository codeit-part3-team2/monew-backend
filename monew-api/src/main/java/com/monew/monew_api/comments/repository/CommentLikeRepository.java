package com.monew.monew_api.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monew.monew_api.comments.entity.CommentLike;

public interface CommentLikeRepository extends JpaRepository<CommentLike,Long> {

	boolean existsByCommentIdAndUser_Id(Long commentId, Long userId);

	void deleteByCommentIdAndUser_Id(Long commentId, Long userId);

}
