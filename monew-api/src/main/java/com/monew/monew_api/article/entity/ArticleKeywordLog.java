package com.monew.monew_api.article.entity;

import com.monew.monew_api.interest.entity.Interest;
import com.monew.monew_api.interest.entity.Keyword;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(
        name = "article_keyword_logs",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_article_keyword_logs_article_keyword_interest",
                        columnNames = {"article_id", "keyword_id", "interest_id"}
                )
        }
)
public class ArticleKeywordLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_id", nullable = false)
    private Interest interest;

    @Column(nullable = false)
    private LocalDateTime collectedAt = LocalDateTime.now();

    public ArticleKeywordLog(Article article, Keyword keyword, Interest interest) {
        this.article = article;
        this.keyword = keyword;
        this.interest = interest;
        this.collectedAt = LocalDateTime.now();
    }
}
