package com.monew.monew_api.article.repository;

import com.monew.monew_api.article.entity.ArticleKeywordLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleKeywordLogRepository extends JpaRepository<ArticleKeywordLog, Long> {

    @Modifying
    @Query(value = """
        INSERT INTO article_keyword_logs (article_id, keyword_id, interest_id, collected_at)
        VALUES (:articleId, :keywordId, :interestId, now())
        ON CONFLICT (article_id, keyword_id, interest_id) DO NOTHING
        """, nativeQuery = true)
    void insertIgnore(
            @Param("articleId") Long articleId,
            @Param("keywordId") Long keywordId,
            @Param("interestId") Long interestId
    );
}
