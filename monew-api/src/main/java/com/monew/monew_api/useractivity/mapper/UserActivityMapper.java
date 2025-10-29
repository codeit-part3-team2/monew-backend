package com.monew.monew_api.useractivity.mapper;

import com.monew.monew_api.comments.entity.Comment;
import com.monew.monew_api.comments.entity.CommentLike;
import com.monew.monew_api.domain.user.User;
import com.monew.monew_api.interest.entity.Interest;
import com.monew.monew_api.subscribe.entit.Subscribe;
import com.monew.monew_api.useractivity.document.UserActivityCacheDocument;
import com.monew.monew_api.useractivity.dto.CommentActivityDto;
import com.monew.monew_api.useractivity.dto.CommentLikeActivityDto;
import com.monew.monew_api.useractivity.dto.SubscribesActivityDto;
import com.monew.monew_api.useractivity.dto.UserActivityDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserActivityMapper {

    @Mapping(target = "id", expression = "java(String.valueOf(user.getId()))")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "nickname", source = "nickname")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "subscriptions", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "commentLikes", ignore = true)
    @Mapping(target = "articleViews", ignore = true)
    UserActivityDto toUserActivityDto(User user);

    @Mapping(target = "id", expression = "java(String.valueOf(subscription.getId()))")
    @Mapping(target = "interestId", expression = "java(String.valueOf(subscription.getInterest().getId()))")
    @Mapping(target = "interestName", source = "interest.name")
    @Mapping(target = "interestKeywords", expression = "java(mapKeywords(subscription.getInterest()))")
    @Mapping(target = "interestSubscriberCount", source = "interest.subscriberCount")
    @Mapping(target = "createdAt", source = "createdAt")
    SubscribesActivityDto toSubscriptionDto(Subscribe subscription);

    List<SubscribesActivityDto> toSubscriptionDtos(List<Subscribe> subscriptions);

    @Mapping(target = "id", expression = "java(String.valueOf(comment.getId()))")
    @Mapping(target = "articleId", expression = "java(String.valueOf(comment.getArticle().getId()))")
    @Mapping(target = "articleTitle", source = "article.title")
    @Mapping(target = "userId", expression = "java(String.valueOf(comment.getUser().getId()))")
    @Mapping(target = "userNickname", source = "user.nickname")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "likeCount", source = "likeCount")
    @Mapping(target = "createdAt", source = "createdAt")
    CommentActivityDto toCommentDto(Comment comment);

    List<CommentActivityDto> toCommentDtos(List<Comment> comments);

    @Mapping(target = "id", expression = "java(String.valueOf(commentLike.getId()))")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "commentId", expression = "java(String.valueOf(commentLike.getComment().getId()))")
    @Mapping(target = "articleId", expression = "java(String.valueOf(commentLike.getComment().getArticle().getId()))")
    @Mapping(target = "articleTitle", source = "comment.article.title")
    @Mapping(target = "commentUserId", expression = "java(String.valueOf(commentLike.getComment().getUser().getId()))")
    @Mapping(target = "commentUserNickname", source = "comment.user.nickname")
    @Mapping(target = "commentContent", source = "comment.content")
    @Mapping(target = "commentLikeCount", source = "comment.likeCount")
    @Mapping(target = "commentCreatedAt", source = "comment.createdAt")
    CommentLikeActivityDto toCommentLikeDto(CommentLike commentLike);

    List<CommentLikeActivityDto> toCommentLikeDtos(List<CommentLike> commentLikes);

    @Mapping(target = "cachedAt", expression = "java(java.time.LocalDateTime.now())")
    UserActivityCacheDocument toDocument(UserActivityDto dto);

    UserActivityDto toDto(UserActivityCacheDocument document);

    default List<String> mapKeywords(Interest interest) {
        return interest.getKeywords().stream()
                .map(ik -> ik.getKeyword().getKeyword())
                .collect(Collectors.toList());
    }
}