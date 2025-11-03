package com.monew.monew_api.article.entity;

import com.monew.monew_api.common.entity.BaseIdEntity;
import com.monew.monew_api.common.exception.article.ArticleNotFoundException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 뉴스 기사 테이블
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "articles")
public class Article extends BaseIdEntity {

    @Column(nullable = false, length = 20)
    private String source;

    @Column(name = "source_url", nullable = false, length = 500, unique = true)
    private String sourceUrl;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "publish_date", nullable = false)
    private LocalDateTime publishDate;

    @Column(nullable = false, length = 200)
    private String summary;

    @Column(name = "comment_count", nullable = false)
    private int commentCount = 0;

    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterestArticles> interestArticles = new ArrayList<>();

    public Article(String source, String sourceUrl, String title, LocalDateTime publishDate, String summary) {
        this.source = source;
        this.sourceUrl = sourceUrl;
        this.title = title;
        this.publishDate = publishDate;
        this.summary = summary;
    }

    public void softDelete() {
        if (this.isDeleted) {
            throw new ArticleNotFoundException();
        }
        this.isDeleted = true;
    }

    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void decreaseCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }

    public void increaseViewCount() {
        this.viewCount++;
    }
}