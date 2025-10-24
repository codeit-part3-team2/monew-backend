package com.monew.monew_api.useractivity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 사용자 활동내역 응답 DTO
 * 사용자의 구독 정보, 최근 작성 댓글, 최근 좋아요, 최근 본 기사를 포함
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityDto {

    private String id;
    private String email;
    private String nickname;
    private LocalDateTime createdAt;

    @Builder.Default
    private List<SubscriptionDto> subscriptions = new ArrayList<>();

    @Builder.Default
    private List<CommentActivityDto> comments = new ArrayList<>();

    @Builder.Default
    private List<CommentLikeActivityDto> commentLikes = new ArrayList<>();

    @Builder.Default
    private List<ArticleViewDto> articleViews = new ArrayList<>();
}