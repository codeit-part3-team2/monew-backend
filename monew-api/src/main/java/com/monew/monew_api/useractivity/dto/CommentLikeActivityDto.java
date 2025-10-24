package com.monew.monew_api.useractivity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentLikeActivityDto {

    private String id;
    private LocalDateTime createdAt;

    private String commentId;
    private String articleId;
    private String articleTitle;

    private UUID commentUserId;
    private String commentUserNickname;
    private String commentContent;
    private Long commentLikeCount;
    private LocalDateTime commentCreatedAt;
}