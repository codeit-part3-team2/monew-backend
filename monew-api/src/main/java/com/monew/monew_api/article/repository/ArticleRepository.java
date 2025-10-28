package com.monew.monew_api.article.repository;

import com.monew.monew_api.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleQueryRepository {

    Optional<Article> findByIdAndIsDeletedFalse(Long id);

    @Query("SELECT DISTINCT a.source FROM Article a WHERE a.isDeleted = false")
    List<String> findDistinctSources();
}
