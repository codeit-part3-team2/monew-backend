package com.monew.monew_api.article.entity;

import com.monew.monew_api.common.entity.BaseCreatedEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 뉴스 기사 조회 테이블
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(
        name = "article_views",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "article_id"}),
        indexes = {
                @Index(name = "ix_article_views_user", columnList = "user_id"),
                @Index(name = "ix_article_views_article", columnList = "article_id")
        }
)
public class ArticleView extends BaseCreatedEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "article_id", nullable = false)
    private Long articleId;
}
