package com.monew.monew_api.article.entity;

import com.monew.monew_api.common.entity.BaseIdEntity;
import com.monew.monew_api.interest.entity.Interest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 기사 - 관심사 연결 테이블
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @OneToMany(mappedBy = "interestArticle", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterestArticleKeyword> interestArticleKeywords = new ArrayList<>();
}
