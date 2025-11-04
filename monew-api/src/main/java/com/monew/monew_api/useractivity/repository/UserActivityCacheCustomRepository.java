package com.monew.monew_api.useractivity.repository;

import com.monew.monew_api.useractivity.dto.ArticleViewActivityDto;
import com.monew.monew_api.useractivity.dto.CommentActivityDto;
import com.monew.monew_api.useractivity.dto.CommentLikeActivityDto;
import com.monew.monew_api.useractivity.dto.SubscribesActivityDto;

import java.util.List;
import java.util.Set;

public interface UserActivityCacheCustomRepository {
    /**
     * 댓글 좋아요 수 증감
     *
     * @param userIds   댓글 좋아요 수를 업데이트할 사용자 ID 집합
     * @param commentId 댓글 ID
     * @param delta     증감 값 (1, -1)
     * @return 업데이트된 캐시 데이터 수
     */
    long incCommentLikeCount(Set<String> userIds, String commentId, int delta);

    /**
     * 기사 조회수 증감
     *
     * @param userIds   기사 조회수을 업데이트할 사용자 ID 집합
     * @param articleId 기사 ID
     * @param delta     증감 값 (1)
     * @return 업데이트된 캐시 데이터 수
     */
    long incArticleViewCount(Set<String> userIds, String articleId, int delta);

    /**
     * 기사 댓글수 증감
     *
     * @param userIds   기사 댓글수를 업데이트할 사용자 ID 집합
     * @param articleId 기사 ID
     * @param delta     증감 값 (1)
     * @return 업데이트된 캐시 데이터 수
     */
    long incArticleCommentCount(Set<String> userIds, String articleId, int delta);

    /**
     * 댓글 좋아요 추가
     *
     * @param userId     사용자 ID
     * @param dto        댓글 좋아요 활동 DTO
     * @param keepLatest 유지할 최신 항목 수
     * @return 업데이트된 캐시 데이터 수
     */
    long pushCommentLike(String userId, CommentLikeActivityDto dto, int keepLatest);

    /**
     * 댓글 좋아요 제거
     *
     * @param userId    사용자 ID
     * @param commentId 댓글 ID
     * @return 업데이트된 캐시 데이터 수
     */
    long pullCommentLike(String userId, String commentId);

    /**
     * 댓글 추가
     *
     * @param userId     사용자 ID
     * @param dto        댓글 활동 DTO
     * @param keepLatest 유지할 최신 항목 수
     * @return 업데이트된 캐시 데이터 수
     */
    long pushComment(String userId, CommentActivityDto dto, int keepLatest);

    /**
     * 댓글 내용 수정
     *
     * @param userIds    사용자 ID 집합
     * @param commentId  댓글 ID
     * @param newContent 새로운 댓글 내용
     * @return 업데이트된 캐시 데이터 수
     */
    long updateCommentContentForUsers(Set<String> userIds, String commentId, String newContent);

    /**
     * 모든 사용자에 대해 댓글 제거
     *
     * @param userIds   사용자 ID 집합
     * @param commentId 댓글 ID
     * @return 업데이트된 캐시 데이터 수
     */
    long removeCommentEverywhere(Set<String> userIds, String commentId);

    /**
     * 기사 조회 활동 추가
     *
     * @param userId     사용자 ID
     * @param dto        기사 조회 활동 DTO
     * @param keepLatest 유지할 최신 항목 수
     * @return 업데이트된 캐시 데이터 수
     */
    long pushArticleView(String userId, ArticleViewActivityDto dto, int keepLatest);

    /**
     * Interest 키워드 업데이트
     *
     * @param interestId  관심사 ID
     * @param newKeywords 새로운 키워드 리스트
     * @return 업데이트된 캐시 데이터 수
     */
    long updateInterestKeywords(String interestId, List<String> newKeywords);

    /**
     * 모든 사용자에 대해 Interest 제거
     *
     * @param userIds    사용자 ID 집합
     * @param interestId 관심사 ID
     * @return 업데이트된 캐시 데이터 수
     */
    long removeInterestEverywhere(Set<String> userIds, String interestId);

    /**
     * 구독 추가
     *
     * @param userId 사용자 ID
     * @param dto    구독 활동 DTO
     * @return 업데이트된 캐시 데이터 수
     */
    long addSubscription(String userId, SubscribesActivityDto dto);

    /**
     * 구독 제거
     *
     * @param userId         사용자 ID
     * @param subscriptionId 구독 ID
     * @return 업데이트된 캐시 데이터 수
     */
    long removeSubscription(String userId, String subscriptionId);
}
