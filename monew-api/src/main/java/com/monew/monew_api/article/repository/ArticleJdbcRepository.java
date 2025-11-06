package com.monew.monew_api.article.repository;

import com.monew.monew_api.article.entity.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArticleJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public boolean insertIgnore(Article article) {
        String sql = """
            INSERT INTO articles (source, source_url, title, summary, publish_date, comment_count, view_count, is_deleted)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (source_url) DO NOTHING
        """;

        int rows = jdbcTemplate.update(sql,
                article.getSource(),
                article.getSourceUrl(),
                article.getTitle(),
                article.getSummary(),
                article.getPublishDate(),
                article.getCommentCount(),
                article.getViewCount(),
                article.isDeleted());

        return rows > 0;
    }
}
