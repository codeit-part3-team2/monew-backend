package com.monew.monew_api.useractivity.repository;

/*
TODO: Entity 클래스 완성 되면 import 수정
 */

import com.monew.monew_api.article.entity.ArticleView;
import com.monew.monew_api.useractivity.dto.ArticleViewActivityDto;
import com.monew.monew_api.useractivity.tempEntity.Comment;
import com.monew.monew_api.useractivity.tempEntity.CommentLike;
import com.monew.monew_api.useractivity.tempEntity.Subscription;

import java.util.List;

public interface UserActivityRepository {
    /*
        활동 내역을 4개의 쿼리로 처리
     */
    List<Subscription> findSubscriptionsByUserId(Long userId);
    List<Comment> findRecentCommentsByUserId(Long userId);
    List<CommentLike> findRecentLikesByUserId(Long userId);
    List<ArticleViewActivityDto> findRecentViewsByUserId(Long userId);

    /*
        활동 내역을 단일 쿼리로 처리
     */
    Object[] findUserActivitiesByUserId(Long userId);
}