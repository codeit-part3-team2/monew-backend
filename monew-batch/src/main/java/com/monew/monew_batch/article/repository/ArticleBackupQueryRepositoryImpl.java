package com.monew.monew_batch.article.repository;


import com.monew.monew_api.article.dto.ArticleBackupData;
import com.monew.monew_api.article.dto.QArticleBackupData_ArticleData;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.monew.monew_api.article.entity.QArticle.article;
import static com.monew.monew_api.article.entity.QInterestArticles.interestArticles;
import static com.monew.monew_api.article.entity.QInterestArticleKeyword.interestArticleKeyword;
import static com.monew.monew_api.interest.entity.QKeyword.keyword1;
import static com.querydsl.core.types.dsl.Expressions.stringTemplate;

/**
 * 뉴스 백업용 QueryDSL 리포지토리
 * - 기사와 연결된 키워드를 한 번에 조회 (N+1 방지)
 * - string_agg()로 키워드 문자열을 집계 후 DTO에서 분리 처리
 */
@Repository
@RequiredArgsConstructor
public class ArticleBackupQueryRepositoryImpl implements ArticleBackupQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ArticleBackupData.ArticleData> findAllArticlesForBackup() {
        return queryFactory
                .select(new QArticleBackupData_ArticleData(
                        article.source,
                        article.sourceUrl,
                        article.title,
                        article.publishDate,
                        article.summary,
                        stringTemplate("string_agg({0}, ',')", keyword1.keyword) // PostgreSQL 집계 함수
                ))
                .from(article)
                .join(article.interestArticles, interestArticles)
                .join(interestArticles.interestArticleKeywords, interestArticleKeyword)
                .join(interestArticleKeyword.keyword, keyword1)
                .where(article.isDeleted.isFalse())
                .groupBy(article.id)
                .fetch();
    }
}