package com.monew.monew_api.useractivity.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class UserActivityDto {
    private String id;
    private String email;
    private String nickname;
    private LocalDateTime createdAt;
    @Setter
    private List<SubscribesActivityDto> subscriptions;
    @Setter
    private List<CommentActivityDto> comments;
    @Setter
    private List<CommentLikeActivityDto> commentLikes;
    @Setter
    private List<ArticleViewActivityDto> articleViews;
}