package com.monew.monew_api.useractivity.service;

import com.monew.monew_api.useractivity.dto.UserActivityDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 캐시 업데이트 서비스 인터페이스
 */
public interface CacheUpdateService {

    /**
     * 댓글 좋아요수 증가/감소
     *
     * @param commentId 댓글 ID
     * @param delta     증가/감소 값
     */
    void updateCommentLikeCount(Long commentId, Integer delta);

    /**
     * 기사 조회수 증가
     *
     * @param articleId 기사 ID
     * @param delta     증가 값
     */
    void incrementArticleViewCount(Long articleId, Integer delta);

    /**
     * 기사 댓글수 증가
     *
     * @param articleId 기사 ID
     * @param delta     증가 값
     */
    void incrementArticleCommentCount(Long articleId, Integer delta);

    /**
     * Interest 정보 업데이트
     *
     * @param interestId  업데이트할 Interest ID
     * @param newKeywords 새로운 키워드 리스트
     */
    void updateInterestKeyword(Long interestId, List<String> newKeywords);

    /**
     * Interest 삭제 처리
     *
     * @param interestId 삭제할 Interest ID
     */
    void removeInterest(Long interestId);

    /**
     * 구독 추가
     *
     * @param userId                  사용자 ID
     * @param subscriptionId          구독 ID
     * @param interestId              관심사 ID
     * @param interestName            관심사 이름
     * @param interestKeywords        관심사 키워드 리스트
     * @param interestSubscriberCount 관심사 구독자 수
     * @param createdAt               구독 생성 일시
     */
    void addSubscription(Long userId, Long subscriptionId, Long interestId, String interestName,
                         List<String> interestKeywords, Integer interestSubscriberCount, LocalDateTime createdAt);

    /**
     * 구독 취소
     *
     * @param userId         사용자 ID
     * @param subscriptionId 구독 ID
     * @param interestId     관심사 ID
     */
    void removeSubscription(Long userId, Long subscriptionId, Long interestId);

    /**
     * 댓글 생성 시 캐시 데이터 + 역인덱스 업데이트
     *
     * @param id           댓글 아이디
     * @param userId       댓글 작성자 아이디
     * @param userNickname 댓글 작성자 닉네임
     * @param articleId    댓글이 작성된 기사 아이디
     * @param articleTitle 댓글이 작성된 기사 제목
     * @param content      댓글 내용
     * @param likeCount    댓글 좋아요 수
     * @param createdAt    댓글 작성 일시
     */
    void addComment(Long id, Long userId, String userNickname, Long articleId, String articleTitle,
                    String content, Integer likeCount, LocalDateTime createdAt);

    /**
     * 좋아요 생성 시 캐시 데이터 + 역인덱스 업데이트
     *
     * @param id                  좋아요 아이디
     * @param userId              좋아요를 누른 사용자 아이디
     * @param createdAt           좋아요 생성 일시
     * @param commentId           좋아요가 눌린 댓글 아이디
     * @param articleId           좋아요가 눌린 댓글이 속한 기사 아이디
     * @param articleTitle        좋아요가 눌린 댓글이 속한 기사 제목
     * @param commentUserId       좋아요가 눌린 댓글 작성자 아이디
     * @param commentUserNickname 좋아요가 눌린 댓글 작성자 닉네임
     * @param commentContent      좋아요가 눌린 댓글 내용
     * @param commentLikeCount    좋아요가 눌린 댓글의 현재 좋아요 수
     * @param commentCreatedAt    좋아요가 눌린 댓글 작성 일시
     */
    void addCommentLike(Long id, Long userId, LocalDateTime createdAt, Long commentId, Long articleId, String articleTitle,
                        Long commentUserId, String commentUserNickname, String commentContent, Integer commentLikeCount,
                        LocalDateTime commentCreatedAt);

    /**
     * 댓글 내용 수정 시 캐시 데이터 + 역인덱스 업데이트
     *
     * @param commentId  댓글 ID
     * @param newContent 새로운 댓글 내용
     */
    void updateCommentContent(Long commentId, String newContent);

    /**
     * 기사 조회 생성 시 캐시 데이터 + 역인덱스 업데이트
     *
     * @param id                   기사 조회 아이디
     * @param userId               기사 조회한 사용자 아이디
     * @param createdAt            기사 조회 일시
     * @param articleId            조회된 기사 아이디
     * @param source               기사 출처
     * @param sourceUrl            기사 출처 URL
     * @param articleTitle         기사 제목
     * @param articlePublishedDate 기사 게시 일시
     * @param articleSummary       기사 요약
     * @param articleCommentCount  댓글 수
     * @param articleViewCount     조회 수
     */
    void addArticleView(Long id,
                        Long userId, LocalDateTime createdAt,
                        Long articleId, String source, String sourceUrl,
                        String articleTitle, LocalDateTime articlePublishedDate,
                        String articleSummary, Integer articleCommentCount,
                        Integer articleViewCount);

    /**
     * 좋아요 삭제 처리
     *
     * @param userId    좋아요를 취소한 사용자 ID
     * @param commentId 좋아요가 취소된 댓글 ID
     */
    void removeCommentLike(Long userId, Long commentId);

    /**
     * 댓글 삭제 처리
     *
     * @param commentId 삭제할 댓글 ID
     */
    void removeComment(Long commentId);

    /**
     * 캐시 저장 (PostgreSQL 조회 후 비동기 저장)
     *
     * @param userId 사용자 ID
     * @param data   사용자 활동 데이터
     */
    void saveCache(String userId, UserActivityDto data);
}