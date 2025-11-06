package com.monew.monew_api.article.repository;

import com.monew.monew_api.article.entity.InterestArticleKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InterestArticleKeywordRepository extends JpaRepository<InterestArticleKeyword, Long> {

    /**
     * 특정 키워드들이 연결된 기사 ID 목록 조회
     */
    @Query("""
        SELECT DISTINCT iak.interestArticle.article.id
        FROM InterestArticleKeyword iak
        WHERE iak.keyword.id IN :keywordIds
    """)
    List<Long> findArticleIdsByKeywordIds(@Param("keywordIds") List<Long> keywordIds);

    /**
     * 주어진 키워드가 아닌 다른 키워드나 관심사에서도
     * 동일한 기사가 사용 중인지 확인
     */
    @Query("""
        SELECT DISTINCT iak.interestArticle.article.id
        FROM InterestArticleKeyword iak
        WHERE iak.interestArticle.article.id IN :articleIds
          AND (iak.interestArticle.interest.id <> :interestId
               OR iak.keyword.id NOT IN :keywordIds)
    """)
    List<Long> findArticlesUsedElsewhere(
            @Param("articleIds") List<Long> articleIds,
            @Param("keywordIds") List<Long> keywordIds,
            @Param("interestId") Long interestId
    );

    @Modifying
    @Query(value = """
        INSERT INTO interest_articles_keywords (interest_article_id, keyword_id)
        VALUES (:interestArticleId, :keywordId)
        ON CONFLICT (interest_article_id, keyword_id) DO NOTHING
        """, nativeQuery = true)
    int insertIgnore(
            @Param("interestArticleId") Long interestArticleId,
            @Param("keywordId") Long keywordId
    );
}
