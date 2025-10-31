package com.monew.monew_api.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.monew.monew_api.comments.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

	// 좋아요 취소
	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
		update Comment c 
		set c.likeCount = case when c.likeCount > 0 then c.likeCount - 1 else 0 end
		where c.id = :id
		""")
	int decLikeCount(@Param("id") Long id);

	// 댓글 물리 삭제
	@Modifying
	@Query(value = "DELETE FROM comments WHERE id = :id AND is_deleted = true", nativeQuery = true)
	int hardDeleteById(@Param("id") Long id);
}
