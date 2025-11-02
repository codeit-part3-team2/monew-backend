package com.monew.monew_api.useractivity.service.Impl;

import com.monew.monew_api.useractivity.document.ReverseIndexDocument;
import com.monew.monew_api.useractivity.document.UserActivityCacheDocument;
import com.monew.monew_api.useractivity.dto.*;
import com.monew.monew_api.useractivity.mapper.UserActivityDocumentMapper;
import com.monew.monew_api.useractivity.repository.UserActivityCacheRepository;
import com.monew.monew_api.useractivity.service.CacheUpdateService;
import com.monew.monew_api.useractivity.service.ReverseIndexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 캐시 업데이트 서비스 구현체
 * MongoDB 캐시를 부분 업데이트
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheUpdateServiceImpl implements CacheUpdateService {

    private final ReverseIndexService reverseIndexService;
    private final UserActivityCacheRepository cacheRepository;
    private final UserActivityDocumentMapper documentMapper;

    @Override
    public void updateCommentLikeCount(Long commentId, Integer delta) {
        Set<String> userIds = reverseIndexService.getUserIds(Set.of(
                ReverseIndexDocument.makeCommentAuthorKey(commentId),
                ReverseIndexDocument.makeCommentLikesKey(commentId)
        ));
        if (userIds.isEmpty()) {
            log.debug("[CacheUpdate] 영향 사용자 없음: commentId={}", commentId);
            return;
        }
        long modified = cacheRepository.incCommentLikeCount(userIds, commentId.toString(), delta);
        log.info("[CacheUpdate] 댓글 좋아요수 업데이트: commentId={}, delta={}, users={}, modified={}",
                commentId, delta, userIds.size(), modified);
    }

    @Override
    public void incrementArticleViewCount(Long articleId, Integer delta) {
        Set<String> viewers = reverseIndexService.getUserIds(
                ReverseIndexDocument.makeArticleViewsKey(articleId)
        );
        if (viewers.isEmpty()) {
            log.debug("[CacheUpdate] 영향 사용자 없음: articleId={}", articleId);
            return;
        }
        long modified = cacheRepository.incArticleViewCount(viewers, articleId.toString(), delta);
        log.info("[CacheUpdate] 기사 조회수 업데이트: articleId={}, delta={}, users={}, modified={}",
                articleId, delta, viewers.size(), modified);
    }

    @Override
    public void incrementArticleCommentCount(Long articleId, Integer delta) {
        Set<String> viewers = reverseIndexService.getUserIds(
                ReverseIndexDocument.makeArticleViewsKey(articleId)
        );
        if (viewers.isEmpty()) {
            log.debug("[CacheUpdate] 영향 사용자 없음: articleId={}", articleId);
            return;
        }
        long modified = cacheRepository.incArticleCommentCount(viewers, articleId.toString(), delta);
        log.info("[CacheUpdate] 기사 댓글수 업데이트: articleId={}, delta={}, users={}, modified={}",
                articleId, delta, viewers.size(), modified);
    }


    @Override
    public void addComment(Long id, Long userId, String userNickname,
                           Long articleId, String articleTitle, String content,
                           Integer likeCount, LocalDateTime createdAt) {

        String uid = userId.toString();
        CommentActivityDto dto = CommentActivityDto.builder()
                .id(id.toString())
                .userId(uid)
                .userNickname(userNickname)
                .articleId(articleId.toString())
                .articleTitle(articleTitle)
                .content(content)
                .likeCount(likeCount)
                .createdAt(createdAt)
                .build();


        long modified = cacheRepository.pushComment(uid, dto, 10);
        if (modified == 0) {
            log.warn("[CacheUpdate] 캐시 없음(만료?): userId={}, commentId={}", uid, id);
        }
        reverseIndexService.addUser(ReverseIndexDocument.makeCommentAuthorKey(id), uid);
        log.info("[CacheUpdate] 댓글 추가: commentId={}, userId={}, modified={}", id, uid, modified);
    }

    @Override
    public void addCommentLike(Long id, Long userId, LocalDateTime createdAt,
                               Long commentId, Long articleId, String articleTitle,
                               Long commentUserId, String commentUserNickname,
                               String commentContent, Integer commentLikeCount,
                               LocalDateTime commentCreatedAt) {
        String uid = userId.toString();
        CommentLikeActivityDto dto = CommentLikeActivityDto.builder()
                .id(id.toString())
                .createdAt(createdAt)
                .commentId(commentId.toString())
                .articleId(articleId.toString())
                .articleTitle(articleTitle)
                .commentUserId(commentUserId.toString())
                .commentUserNickname(commentUserNickname)
                .commentContent(commentContent)
                .commentLikeCount(commentLikeCount)
                .commentCreatedAt(commentCreatedAt)
                .build();

        long modified = cacheRepository.pushCommentLike(uid, dto, 10);
        if (modified == 0) {
            log.warn("[CacheUpdate] 캐시 없음(만료?): userId={}, likeId={}", uid, id);
        }
        reverseIndexService.addUser(ReverseIndexDocument.makeCommentLikesKey(commentId), uid);
        log.info("[CacheUpdate] 댓글 좋아요 추가: commentId={}, userId={}, modified={}", commentId, uid, modified);
    }

    @Override
    public void updateCommentContent(Long commentId, String newContent) {
        Set<String> userIds = reverseIndexService.getUserIds(Set.of(
                ReverseIndexDocument.makeCommentAuthorKey(commentId),
                ReverseIndexDocument.makeCommentLikesKey(commentId)
        ));
        if (userIds.isEmpty()) {
            log.debug("[CacheUpdate] 댓글 내용 수정 영향 사용자 없음: commentId={}", commentId);
            return;
        }
        long modified = cacheRepository.updateCommentContentForUsers(userIds, commentId.toString(), newContent);
        log.info("[CacheUpdate] 댓글 내용 수정 반영: commentId={}, users={}, modified={}",
                commentId, userIds.size(), modified);
    }

    @Override
    public void removeCommentLike(Long userId, Long commentId) {
        String uid = userId.toString();
        long modified = cacheRepository.pullCommentLike(uid, commentId.toString());
        reverseIndexService.removeUser(ReverseIndexDocument.makeCommentLikesKey(commentId), uid);
        log.info("[CacheUpdate] 댓글 좋아요 제거: commentId={}, userId={}, modified={}", commentId, uid, modified);
    }

    @Override
    public void addArticleView(Long id, Long userId, LocalDateTime createdAt,
                               Long articleId, String source, String sourceUrl,
                               String articleTitle, LocalDateTime articlePublishedDate,
                               String articleSummary, Integer articleCommentCount,
                               Integer articleViewCount) {
        String uid = userId.toString();
        ArticleViewActivityDto dto = ArticleViewActivityDto.builder()
                .id(id.toString())
                .viewedBy(uid)
                .createdAt(createdAt)
                .articleId(articleId.toString())
                .source(source)
                .sourceUrl(sourceUrl)
                .articleTitle(articleTitle)
                .articlePublishedDate(articlePublishedDate)
                .articleSummary(articleSummary)
                .articleCommentCount(articleCommentCount)
                .articleViewCount(articleViewCount)
                .build();

        long modified = cacheRepository.pushArticleView(uid, dto, 10);
        if (modified == 0) {
            log.warn("[CacheUpdate] 캐시 없음(만료?): userId={}, viewId={}", uid, id);
        }
        reverseIndexService.addUser(ReverseIndexDocument.makeArticleViewsKey(articleId), uid);
        log.info("[CacheUpdate] 기사 조회 추가: articleId={}, userId={}, modified={}", articleId, uid, modified);
    }

    /*
     * version 이전인 경우에만 관심사 키워드 업데이트
     */
    @Override
    public void updateInterestKeyword(Long interestId, List<String> newKeywords) {
        String iid = String.valueOf(interestId);
        long modified = cacheRepository.updateInterestKeywords(iid, newKeywords);
        log.info("[CacheUpdate] Interest 키워드 갱신(set): interestId={}, modified={}", iid, modified);
    }

    @Override
    public void removeInterest(Long interestId) {
        String id = String.valueOf(interestId);

        Set<String> userIds = reverseIndexService.getUserIds(
                ReverseIndexDocument.makeInterestSubscribersKey(interestId)
        );

        long modified = 0;
        if (!userIds.isEmpty()) {
            modified = cacheRepository.removeInterestEverywhere(userIds, id);
        }

        reverseIndexService.deleteIndexes(Set.of(ReverseIndexDocument.makeInterestSubscribersKey(interestId)));

        log.info("[CacheUpdate] 관심사 삭제 반영: interestId={}, users={}, modified={}", id, userIds.size(), modified);
    }

    @Override
    public void addSubscription(Long userId,
                                Long subscriptionId,
                                Long interestId,
                                String interestName,
                                List<String> interestKeywords,
                                Integer interestSubscriberCount,
                                LocalDateTime createdAt) {

        String uid = String.valueOf(userId);
        SubscribesActivityDto dto = SubscribesActivityDto.builder()
                .id(String.valueOf(subscriptionId))
                .interestId(String.valueOf(interestId))
                .interestName(interestName)
                .interestKeywords(interestKeywords)
                .interestSubscriberCount(interestSubscriberCount)
                .createdAt(createdAt)
                .build();

        long modified = cacheRepository.addSubscription(uid, dto);
        reverseIndexService.addUser(ReverseIndexDocument.makeInterestSubscribersKey(interestId), uid);
        log.info("[CacheUpdate] 구독 추가: userId={}, subId={}, interestId={}, modified={}",
                uid, subscriptionId, interestId, modified);
    }

    @Override
    public void removeSubscription(Long userId, Long subscriptionId, Long interestId) {
        String uid = String.valueOf(userId);
        long modified = cacheRepository.removeSubscription(uid, subscriptionId.toString());
        reverseIndexService.removeUser(ReverseIndexDocument.makeInterestSubscribersKey(interestId), uid);
        log.info("[CacheUpdate] 구독 제거: userId={}, subId={}, modified={}", uid, subscriptionId, modified);
    }

    @Override
    public void removeComment(Long commentId) {
        Set<String> userIds = reverseIndexService.getUserIds(Set.of(
                ReverseIndexDocument.makeCommentAuthorKey(commentId),
                ReverseIndexDocument.makeCommentLikesKey(commentId)
        ));
        if (userIds.isEmpty()) {
            log.debug("[CacheUpdate] 영향 사용자 없음: commentId={}", commentId);
            return;
        }
        long modified = cacheRepository.removeCommentEverywhere(userIds, commentId.toString());
        log.info("[CacheUpdate] 댓글 삭제 캐시 반영: commentId={}, users={}, modified={}",
                commentId, userIds.size(), modified);

        reverseIndexService.deleteIndexes(Set.of(
                ReverseIndexDocument.makeCommentAuthorKey(commentId),
                ReverseIndexDocument.makeCommentLikesKey(commentId)
        ));
    }

    @Override
    public void saveCache(String userId, UserActivityDto data) {
        log.info("[CacheUpdate] 캐시 저장 시작: userId={}", userId);
        UserActivityCacheDocument doc = documentMapper.toDocument(data);
        cacheRepository.save(doc);
        log.debug("[CacheUpdate] 캐시 저장 완료: userId={}", userId);

        buildReverseIndexes(userId, data);
        log.info("[CacheUpdate] 캐시 및 역인덱스 저장 완료: userId={}", userId);
    }

    /**
     * 역인덱스 초기 생성
     */
    private void buildReverseIndexes(String userId, UserActivityDto data) {
        data.getComments().forEach(comment -> {
            reverseIndexService.addUser(
                    ReverseIndexDocument.makeCommentAuthorKey(Long.parseLong(comment.getId())),
                    userId
            );
        });

        data.getCommentLikes().forEach(like -> {
            reverseIndexService.addUser(
                    ReverseIndexDocument.makeCommentLikesKey(Long.parseLong(like.getCommentId())),
                    userId
            );
        });

        data.getArticleViews().forEach(view -> {
            reverseIndexService.addUser(
                    ReverseIndexDocument.makeArticleViewsKey(Long.parseLong(view.getArticleId())),
                    userId
            );
        });

        data.getSubscriptions().forEach(sub -> {
            reverseIndexService.addUser(
                    ReverseIndexDocument.makeInterestSubscribersKey(Long.parseLong(sub.getInterestId())),
                    userId
            );
        });

        log.info("[CacheUpdate] 역인덱스 생성 완료: userId={}, 댓글작성={}개, 좋아요={}개, 기사조회={}개, 구독={}개",
                userId,
                data.getComments().size(),
                data.getCommentLikes().size(),
                data.getArticleViews().size(),
                data.getSubscriptions().size());


    }
}
