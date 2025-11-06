package com.monew.monew_api.article.entity;

import com.monew.monew_api.common.entity.BaseIdEntity;
import com.monew.monew_api.interest.entity.Keyword;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "interest_articles_keywords",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_interest_articles_keywords",
                columnNames = {"interest_article_id", "keyword_id"}
        )
)
public class InterestArticleKeyword extends BaseIdEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_article_id", nullable = false)
    private InterestArticles interestArticle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword;
}
