package com.monew.monew_api.useractivity.repository;

import com.monew.monew_api.useractivity.dto.ArticleViewActivityDto;
import com.monew.monew_api.useractivity.dto.CommentActivityDto;
import com.monew.monew_api.useractivity.dto.CommentLikeActivityDto;
import com.monew.monew_api.useractivity.dto.SubscribesActivityDto;

import java.util.List;
import java.util.Set;

public interface UserActivityCacheCustomRepository {

    long incCommentLikeCount(Set<String> userIds, String commentId, int delta);

    long incArticleViewCount(Set<String> userIds, String articleId, int delta);

    long incArticleCommentCount(Set<String> userIds, String articleId, int delta);

    long pushCommentLike(String userId, CommentLikeActivityDto dto, int keepLatest);

    long pullCommentLike(String userId, String commentId);

    long pushComment(String userId, CommentActivityDto dto, int keepLatest);

    long updateCommentContentForUsers(Set<String> userIds, String commentId, String newContent);

    long removeCommentEverywhere(Set<String> userIds, String commentId);

    long pushArticleView(String userId, ArticleViewActivityDto dto, int keepLatest);

    long updateInterestKeywords(String interestId, List<String> newKeywords);

    long removeInterestEverywhere(Set<String> userIds, String interestId);

    long addSubscription(String userId, SubscribesActivityDto dto);

    long removeSubscription(String userId, String subscriptionId);
}
