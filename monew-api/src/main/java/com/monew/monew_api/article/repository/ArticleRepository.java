package com.monew.monew_api.article.repository;

import com.monew.monew_api.article.entity.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleQueryRepository {

    Optional<Article> findByIdAndIsDeletedFalse(Long id);

    @Query("SELECT DISTINCT a.source FROM Article a WHERE a.isDeleted = false")
    List<String> findDistinctSources();

    Optional<Article> findBySourceUrl(String sourceUrl);

    @Modifying
    @Query("""
        UPDATE Article a
        SET a.isDeleted = false
        WHERE a.sourceUrl = :sourceUrl AND a.isDeleted = true
    """)
    int restoreIfDeleted(@Param("sourceUrl") String sourceUrl);

    List<Article> findAllByIsDeletedTrue();

/*
    @Modifying
    @Query(value = """
        INSERT INTO articles (source, source_url, title, summary, publish_date, comment_count, view_count, is_deleted)
        VALUES (:#{#a.source}, :#{#a.sourceUrl}, :#{#a.title}, :#{#a.summary},
                :#{#a.publishDate}, :#{#a.commentCount}, :#{#a.viewCount}, :#{#a.isDeleted})
        ON CONFLICT (source_url) DO NOTHING
        """, nativeQuery = true)
    void insertIgnore(@Param("a") Article article);
*/
}
