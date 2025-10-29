package com.monew.monew_api.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.monew.monew_api.comments.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

	@Modifying(clearAutomatically = true, flushAutomatically = true)
	@Query("""
         update Comment c 
         set c.likeCount = case when c.likeCount > 0 then c.likeCount - 1 else 0 end
         where c.id = :id
         """)
	int decLikeCount(@Param("id") Long id);

	@Modifying
	@Query(value = "DELETE FROM comments WHERE id = :id", nativeQuery = true)
	void hardDeleteById(@Param("id") Long id);
}
