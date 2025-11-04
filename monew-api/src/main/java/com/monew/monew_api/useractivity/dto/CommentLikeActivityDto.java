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
public class CommentLikeActivityDto {

    @JsonProperty("id")
    @JsonAlias({"like_id"})
    private String id;

    @JsonProperty("createdAt")
    @JsonAlias({"created_at"})
    private LocalDateTime createdAt;

    @JsonProperty("commentId")
    @JsonAlias({"comment_id"})
    private String commentId;

    @JsonProperty("articleId")
    @JsonAlias({"article_id"})
    private String articleId;

    @JsonProperty("articleTitle")
    @JsonAlias({"article_title"})
    private String articleTitle;

    @JsonProperty("commentAuthorId")
    @JsonAlias({"comment_user_id"})
    private String commentUserId;

    @JsonProperty("commentUserNickname")
    @JsonAlias({"comment_user_nickname"})
    private String commentUserNickname;

    @JsonProperty("commentContent")
    @JsonAlias({"comment_content"})
    private String commentContent;

    @JsonProperty("commentLikeCount")
    @JsonAlias({"comment_like_count"})
    private Integer commentLikeCount;

    @JsonProperty("commentCreatedAt")
    @JsonAlias({"comment_created_at"})
    private LocalDateTime commentCreatedAt;
}