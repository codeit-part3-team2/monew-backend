package com.monew.monew_api.useractivity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityDto {
    private String id;
    private String email;
    private String nickname;
    private LocalDateTime createdAt;
    private List<SubscribesActivityDto> subscriptions;
    private List<CommentActivityDto> comments;
    private List<CommentLikeActivityDto> commentLikes;
    private List<ArticleViewActivityDto> articleViews;
}