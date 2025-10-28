package com.monew.monew_api.comments.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monew.monew_api.comments.entity.CommentLike;

public interface CommentLikeRepository extends JpaRepository<CommentLike,Long> {

	boolean existsByComment_IdAndUser_Id(Long commentId, Long userId);

	void deleteByComment_IdAndUser_Id(Long commentId, Long userId);

	// N+1 회피용: 특정 사용자 + 여러 댓글 ID에 대한 좋아요 목록
	List<CommentLike> findByUser_IdAndComment_IdIn(Long userId, Collection<Long> commentIds);

}
