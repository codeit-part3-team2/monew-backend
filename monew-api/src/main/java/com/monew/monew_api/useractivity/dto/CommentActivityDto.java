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
public class CommentActivityDto {

    private String id;
    private String articleId;
    private String articleTitle;
    private String userId;
    private String userNickname;
    private String content;
    private Long likeCount;
    private LocalDateTime createdAt;
}