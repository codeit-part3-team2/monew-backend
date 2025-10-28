package com.monew.monew_api.useractivity.mapper;

import com.monew.monew_api.article.entity.Interest;
import com.monew.monew_api.useractivity.dto.*;
import com.monew.monew_api.domain.user.User;
import com.monew.monew_api.useractivity.tempEntity.Comment;
import com.monew.monew_api.useractivity.tempEntity.CommentLike;
import com.monew.monew_api.useractivity.tempEntity.Subscription;
import org.mapstruct.*;

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
    SubscribesActivityDto toSubscriptionDto(Subscription subscription);

    List<SubscribesActivityDto> toSubscriptionDtos(List<Subscription> subscriptions);

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

//    @Mapping(target = "id", expression = "java(String.valueOf(articleView.getId()))")
//    @Mapping(target = "viewedBy", expression = "java(String.valueOf(articleView.getUserId()))")
//    @Mapping(target = "createdAt", source = "createdAt")
//    @Mapping(target = "articleId", expression = "java(String.valueOf(articleView.getArticle().getId()))")
//    @Mapping(target = "source", source = "article.source")
//    @Mapping(target = "sourceUrl", source = "article.sourceUrl")
//    @Mapping(target = "articleTitle", source = "article.title")
//    @Mapping(target = "articlePublishedDate", source = "article.publishDate")
//    @Mapping(target = "articleSummary", source = "article.summary")
//    @Mapping(target = "articleCommentCount", source = "article.commentCount")
//    @Mapping(target = "articleViewCount", source = "article.viewCount")
//    ArticleViewActivityDto toArticleViewDto(ArticleView articleView);
//
//    List<ArticleViewActivityDto> toArticleViewActivityDtos(List<ArticleView> articleViews);

    default List<String> mapKeywords(Interest interest) {
        return interest.getInterestsKeywords().stream()
                .map(ik -> ik.getKeywordEntity().getKeyword())
                .collect(Collectors.toList());
    }
}