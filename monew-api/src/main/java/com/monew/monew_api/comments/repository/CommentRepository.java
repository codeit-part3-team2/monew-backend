package com.monew.monew_api.comments.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.monew.monew_api.comments.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findByArticleIdOrderByCreatedAtDesc(Long articleId);

}
