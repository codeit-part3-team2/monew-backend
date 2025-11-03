package com.monew.monew_api.article.repository;

import com.monew.monew_api.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleQueryRepository {

    /** 논리 삭제되지 않은 기사 단건 조회 */
    Optional<Article> findByIdAndIsDeletedFalse(Long id);

    /** 기사 출처(source) 중복 없이 조회 */
    @Query("SELECT DISTINCT a.source FROM Article a WHERE a.isDeleted = false")
    List<String> findDistinctSources();

    /** 기사 URL로 중복 여부 확인 (뉴스 중복 방지용) */
    Optional<Article> findBySourceUrl(String sourceUrl);

    /** 여러 기사 논리 삭제 (isDeleted = true) */
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE Article a
        SET a.isDeleted = true
        WHERE a.id IN :articleIds
    """)
    void markAsDeleted(@Param("articleIds") List<Long> articleIds);

    /** 논리 삭제된 기사 전체 조회 (스케줄러 등에서 사용) */
    List<Article> findAllByIsDeletedTrue();

    @Query("SELECT a.sourceUrl FROM Article a")
    Set<String> findAllSourceUrls();
}
