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
public class CommentActivityDto {

    @JsonProperty("id")
    @JsonAlias({"comment_id"})
    private String id;

    @JsonProperty("articleId")
    @JsonAlias({"article_id"})
    private String articleId;

    @JsonProperty("articleTitle")
    @JsonAlias({"article_title"})
    private String articleTitle;

    @JsonProperty("userId")
    @JsonAlias({"user_id"})
    private String userId;

    @JsonProperty("userNickname")
    @JsonAlias({"user_nickname"})
    private String userNickname;

    @JsonProperty("content")
    private String content;

    @JsonProperty("likeCount")
    @JsonAlias({"like_count"})
    private Integer likeCount;

    @JsonProperty("createdAt")
    @JsonAlias({"created_at"})
    private LocalDateTime createdAt;
}