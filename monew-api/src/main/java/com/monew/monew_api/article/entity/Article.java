package com.monew.monew_api.article.entity;

import com.monew.monew_api.common.entity.BaseIdEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 뉴스 기사 테이블
 */
@Getter
@Setter
@NoArgsConstructor
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
}