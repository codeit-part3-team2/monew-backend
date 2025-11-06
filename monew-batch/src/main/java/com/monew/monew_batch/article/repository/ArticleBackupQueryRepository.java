package com.monew.monew_batch.article.repository;

import com.monew.monew_api.article.dto.ArticleBackupData;

import java.util.List;

public interface ArticleBackupQueryRepository {

    List<ArticleBackupData.ArticleData> findAllArticlesForBackup();
}
