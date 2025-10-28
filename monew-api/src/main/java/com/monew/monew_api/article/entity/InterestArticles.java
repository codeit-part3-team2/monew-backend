package com.monew.monew_api.article.entity;

import com.monew.monew_api.common.entity.BaseIdEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 기사 - 관심사 연결 테이블
 */
@Getter
@NoArgsConstructor
@Entity
@Table(
        name = "interest_articles",
        uniqueConstraints = @UniqueConstraint(columnNames = {"article_id", "interest_id"})
)
public class InterestArticles extends BaseIdEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_id", nullable = false)
    private Interest interest;
}
