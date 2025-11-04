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
        UserActivityDto {
            User
            findSubscriptionsByUserId()
            findRecentCommentsByUserId()
            findRecentLikesByUserId()
            findRecentViewsByUserId()
        } 형태로 구성
     */

    /**
     * 사용자의 구독 정보 조회
     * @param userId 사용자 ID
     * @return 구독 정보 리스트
     */
    List<Subscribe> findSubscriptionsByUserId(Long userId);

    /**
     * 사용자의 최근 댓글 조회
     * @param userId 사용자 ID
     * @return 댓글 리스트
     */
    List<Comment> findRecentCommentsByUserId(Long userId);

    /**
     * 사용자의 최근 댓글 좋아요 조회
     * @param userId 사용자 ID
     * @return 댓글 좋아요 리스트
     */
    List<CommentLike> findRecentLikesByUserId(Long userId);

    /**
     * 사용자의 최근 기사 조회
     * @param userId 사용자 ID
     * @return 기사 조회 리스트
     */
    List<ArticleViewActivityDto> findRecentViewsByUserId(Long userId);

    /*
        record 사용한 단일 쿼리
     */

    /**
     * 사용자 활동내역 단일 쿼리 조회
     * @param userId 사용자 ID
     * @return 사용자 활동내역 프로젝션
     */
    UserActivityRaw findUserActivityRaw(Long userId);
}