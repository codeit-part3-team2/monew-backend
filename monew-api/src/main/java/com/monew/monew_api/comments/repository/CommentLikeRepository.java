package com.monew.monew_api.comments.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monew.monew_api.comments.entity.CommentLike;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

	// 좋아요 중복 확인
	boolean existsByComment_IdAndUser_Id(Long commentId, Long userId);

	// 자신 좋아요 취소
	void deleteByComment_IdAndUser_Id(Long commentId, Long userId);

}
