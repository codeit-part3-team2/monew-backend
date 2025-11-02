package com.monew.monew_api.useractivity.service;

import com.monew.monew_api.useractivity.dto.UserActivityDto;
import org.hibernate.mapping.Set;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 캐시 업데이트 서비스 인터페이스
 */
public interface CacheUpdateService {

    /**
     * 댓글 좋아요수 증가/감소
     */
    void updateCommentLikeCount(Long commentId, Integer delta);

    /**
     * 기사 조회수 증가
     */
    void incrementArticleViewCount(Long articleId, Integer delta);

    /**
     * 기사 댓글수 증가
     */
    void incrementArticleCommentCount(Long articleId, Integer delta);

    /**
     * Interest 정보 업데이트
     */
    void updateInterestKeyword(Long interestId, List<String> newKeywords);

    /**
     * Interest 삭제 처리
     * @param interestId
     */
    void removeInterest(Long interestId);

    /**
     * 구독 추가
     */
    void addSubscription(Long userId, Long subscriptionId, Long interestId, String interestName,
                         List<String> interestKeywords, Integer interestSubscriberCount, LocalDateTime createdAt);

    /**
     * 구독 취소
     */
    void removeSubscription(Long userId, Long subscriptionId, Long interestId);

    /**
     * 댓글 생성 시 캐시 데이터 + 역인덱스 업데이트
     * @param id
     * @param userId
     * @param userNickname
     * @param articleId
     * @param articleTitle
     * @param content
     * @param likeCount
     * @param createdAt
     */
    void addComment(Long id, Long userId, String userNickname, Long articleId, String articleTitle,
                    String content, Integer likeCount, LocalDateTime createdAt);

    /**
     * 좋아요 생성 시 캐시 데이터 + 역인덱스 업데이트
     * @param id
     * @param userId
     * @param createdAt
     * @param commentId
     * @param articleId
     * @param articleTitle
     * @param commentUserId
     * @param commentUserNickname
     * @param commentContent
     * @param commentLikeCount
     * @param commentCreatedAt
     */
    void addCommentLike(Long id, Long userId, LocalDateTime createdAt, Long commentId, Long articleId, String articleTitle,
                        Long commentUserId, String commentUserNickname, String commentContent, Integer commentLikeCount,
                        LocalDateTime commentCreatedAt);

    /**
     * 댓글 내용 수정 시 캐시 데이터 + 역인덱스 업데이트
     * @param commentId
     * @param newContent
     */
    void updateCommentContent(Long commentId, String newContent);

    /**
     * 기사 조회 생성 시 캐시 데이터 + 역인덱스 업데이트
     * @param id
     * @param userId
     * @param createdAt
     * @param articleId
     * @param source
     * @param sourceUrl
     * @param articleTitle
     * @param articlePublishedDate
     * @param articleSummary
     * @param articleCommentCount
     * @param articleViewCount
     */
    void addArticleView(Long id,
                        Long userId, LocalDateTime createdAt,
                        Long articleId, String source, String sourceUrl,
                        String articleTitle, LocalDateTime articlePublishedDate,
                        String articleSummary, Integer articleCommentCount,
                        Integer articleViewCount);

    /**
     * 좋아요 삭제 처리
     * @param userId
     * @param commentId
     */
    void removeCommentLike(Long userId, Long commentId);
    /**
     * 댓글 삭제 처리
     */
    void removeComment(Long commentId);

    /**
     * 캐시 저장 (PostgreSQL 조회 후 비동기 저장)
     */
    void saveCache(String userId, UserActivityDto data);
}