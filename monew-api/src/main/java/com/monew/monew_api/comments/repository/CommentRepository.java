package com.monew.monew_api.comments.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.monew.monew_api.comments.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

}
