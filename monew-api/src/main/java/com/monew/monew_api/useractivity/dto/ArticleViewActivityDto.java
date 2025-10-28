package com.monew.monew_api.useractivity.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleViewActivityDto {

    @JsonProperty("id")
    @JsonAlias({"view_id"})
    private String id;

    @JsonProperty("viewedBy")
    @JsonAlias({"viewed_by"})
    private String viewedBy;

    @JsonProperty("createdAt")
    @JsonAlias({"created_at"})
    private LocalDateTime createdAt;

    @JsonProperty("articleId")
    @JsonAlias({"article_id"})
    private String articleId;

    @JsonProperty("source")
    private String source;

    @JsonProperty("sourceUrl")
    @JsonAlias({"source_url"})
    private String sourceUrl;

    @JsonProperty("articleTitle")
    @JsonAlias({"article_title"})
    private String articleTitle;

    @JsonProperty("articlePublishedDate")
    @JsonAlias({"publish_date", "article_published_date"})
    private LocalDateTime articlePublishedDate;

    @JsonProperty("articleSummary")
    @JsonAlias({"summary", "article_summary"})
    private String articleSummary;

    @JsonProperty("articleCommentCount")
    @JsonAlias({"comment_count", "article_comment_count"})
    private Integer articleCommentCount;

    @JsonProperty("articleViewCount")
    @JsonAlias({"view_count", "article_view_count"})
    private Integer articleViewCount;
}