package com.monew.monew_api.useractivity.document;

import com.monew.monew_api.useractivity.dto.ArticleViewActivityDto;
import com.monew.monew_api.useractivity.dto.CommentActivityDto;
import com.monew.monew_api.useractivity.dto.CommentLikeActivityDto;
import com.monew.monew_api.useractivity.dto.SubscribesActivityDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "user_activity_cache")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityCacheDocument {

    @Id
    private String id;

    private String email;
    private String nickname;
    private LocalDateTime createdAt;

    private List<SubscribesActivityDto> subscriptions;
    private List<CommentActivityDto> comments;
    private List<CommentLikeActivityDto> commentLikes;
    private List<ArticleViewActivityDto> articleViews;

    @Indexed(expireAfter = "1h")
    private LocalDateTime cachedAt;
}