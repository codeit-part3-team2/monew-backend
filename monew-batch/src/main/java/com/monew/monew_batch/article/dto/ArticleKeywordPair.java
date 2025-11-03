package com.monew.monew_batch.article.dto;

import com.monew.monew_api.article.entity.Article;
import com.monew.monew_api.interest.entity.Keyword;

public record ArticleKeywordPair(
        Article article,
        Keyword keyword
) {}