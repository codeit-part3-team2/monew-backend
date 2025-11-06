package com.monew.monew_api.article.repository;

import com.monew.monew_api.article.entity.ArticleView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleViewRepository extends JpaRepository<ArticleView, Long> {

    boolean existsByUserIdAndArticleId(Long userId, Long articleId);
}
