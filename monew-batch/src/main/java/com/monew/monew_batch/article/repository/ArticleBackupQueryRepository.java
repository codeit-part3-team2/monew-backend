package com.monew.monew_batch.article.repository;

import com.monew.monew_api.article.dto.NewsBackupData;

import java.util.List;

public interface ArticleBackupQueryRepository {

    List<NewsBackupData.ArticleData> findAllArticlesForBackup();
}
