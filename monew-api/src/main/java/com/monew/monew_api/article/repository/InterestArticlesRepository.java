package com.monew.monew_api.article.repository;

import com.monew.monew_api.article.entity.Article;
import com.monew.monew_api.article.entity.InterestArticles;
import com.monew.monew_api.interest.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InterestArticlesRepository extends JpaRepository<InterestArticles, Long> {

    /** 특정 관심사(interestId)에 연결된 모든 기사 ID 조회 */
    @Query("""
        SELECT ia.article.id
        FROM InterestArticles ia
        WHERE ia.interest.id = :interestId
    """)
    List<Long> findArticleIdsByInterestId(@Param("interestId") Long interestId);

    /**
     * 주어진 기사들(articleIds)이
     * 현재 관심사(interestId)를 제외한 다른 관심사에서도 사용 중인지 확인.
     * (즉, “공유된 기사”를 식별하기 위한 쿼리)
     */
    @Query("""
        SELECT DISTINCT ia.article.id
        FROM InterestArticles ia
        WHERE ia.article.id IN :articleIds
          AND ia.interest.id <> :interestId
    """)
    List<Long> findArticleIdsUsedByOtherInterests(
            @Param("articleIds") List<Long> articleIds,
            @Param("interestId") Long interestId
    );

    /** 특정 기사와 관심사 간의 연결이 이미 존재하는지 확인 */
    Optional<InterestArticles> findByArticleAndInterest(Article article, Interest interest);

    @Modifying
    @Query(
            value = """
            INSERT INTO interest_articles (interest_id, article_id, created_at, updated_at)
            VALUES (:interestId, :articleId, NOW(), NOW())
            ON CONFLICT (interest_id, article_id) DO NOTHING
        """,
            nativeQuery = true
    )
    int insertIgnore(
            @Param("interestId") Long interestId,
            @Param("articleId") Long articleId);
}
