package com.monew.monew_api.article.repository;

import com.monew.monew_api.article.entity.Article;
import com.monew.monew_api.article.entity.InterestArticles;
import com.monew.monew_api.interest.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestArticlesRepository extends JpaRepository<InterestArticles, Long> {

    boolean existsByArticleAndInterest(Article article, Interest interest);
}
