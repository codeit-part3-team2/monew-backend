package com.monew.monew_api.useractivity.repository;

/*
TODO: Entity 클래스 완성 되면 import 수정
 */

import com.monew.monew_api.comments.entity.Comment;
import com.monew.monew_api.comments.entity.CommentLike;
import com.monew.monew_api.subscribe.entity.Subscribe;
import com.monew.monew_api.useractivity.dto.ArticleViewActivityDto;
import com.monew.monew_api.useractivity.repository.projection.UserActivityRaw;

import java.util.List;

public interface UserActivityRepository {
    /*
        활동 내역을 4개의 쿼리로 처리
     */
    List<Subscribe> findSubscriptionsByUserId(Long userId);

    List<Comment> findRecentCommentsByUserId(Long userId);

    List<CommentLike> findRecentLikesByUserId(Long userId);

    List<ArticleViewActivityDto> findRecentViewsByUserId(Long userId);

    /*
        record 사용한 단일 쿼리
     */
    UserActivityRaw findUserActivityRaw(Long userId);
}