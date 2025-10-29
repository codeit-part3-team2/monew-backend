package com.monew.monew_api.useractivity.service.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monew.monew_api.comments.entity.Comment;
import com.monew.monew_api.comments.entity.CommentLike;
import com.monew.monew_api.common.exception.user.UserNotFoundException;
import com.monew.monew_api.subscribe.entit.Subscribe;
import com.monew.monew_api.useractivity.document.UserActivityCacheDocument;
import com.monew.monew_api.useractivity.dto.*;
import com.monew.monew_api.useractivity.mapper.UserActivityMapper;
import com.monew.monew_api.useractivity.repository.UserActivityCacheRepository;
import com.monew.monew_api.useractivity.repository.UserActivityRepository;
import com.monew.monew_api.useractivity.service.UserActivityService;
import com.monew.monew_api.domain.user.User;
import com.monew.monew_api.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.messaging.Subscription;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserActivityServiceImpl implements UserActivityService {

    private final UserRepository userRepository;
    private final UserActivityRepository activityRepository;
    private final UserActivityCacheRepository cacheRepository;
    private final UserActivityMapper mapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public UserActivityDto getUserActivity(String userId) {
        log.info("사용자 활동내역 조회 시작: userId={}", userId);

        Long userIdLong = Long.parseLong(userId);

        User user = userRepository.findById(userIdLong)
                .orElseThrow(UserNotFoundException::new);

        List<Subscribe> subscriptions = activityRepository.findSubscriptionsByUserId(userIdLong);
        log.info("구독 정보 조회 완료: {}건", subscriptions.size());

        List<Comment> comments = activityRepository.findRecentCommentsByUserId(userIdLong);
        log.info("최근 댓글 조회 완료: {}건", comments.size());

        List<CommentLike> likes = activityRepository.findRecentLikesByUserId(userIdLong);
        log.info("최근 좋아요 조회 완료: {}건", likes.size());

        List<ArticleViewActivityDto> views = activityRepository.findRecentViewsByUserId(userIdLong);
        log.info("최근 조회 기사 조회 완료: {}건", views.size());

        UserActivityDto result = mapper.toUserActivityDto(user);
        result.setSubscriptions(mapper.toSubscriptionDtos(subscriptions));
        result.setComments(mapper.toCommentDtos(comments));
        result.setCommentLikes(mapper.toCommentLikeDtos(likes));
        result.setArticleViews(views);

        log.info("사용자 활동내역 조회 완료: userId={}", userId);
        return result;
    }

    /**
     * 추가: 단일 쿼리 방식
     */
    @Override
    @Transactional(readOnly = true)
    public UserActivityDto getUserActivitySingleQuery(String userId) {
        log.info("사용자 활동내역 조회 시작 (단일 쿼리): userId={}", userId);

        Long userIdLong = Long.parseLong(userId);

        // User 존재 확인
        userRepository.findById(userIdLong)
                .orElseThrow(UserNotFoundException::new);

        // Repository에서 Raw 데이터 가져오기
        Object[] rawData = activityRepository.findUserActivitiesByUserId(userIdLong);

        if (rawData == null) {
            log.error("사용자 활동 데이터를 찾을 수 없음: userId={}", userId);
            throw new UserNotFoundException();
        }

        // Raw 데이터 파싱 및 DTO 매핑
        UserActivityDto result = parseRawDataToDto(rawData, userIdLong);

        log.info("사용자 활동내역 조회 완료 (단일 쿼리): userId={}, 구독: {}건, 댓글: {}건, 좋아요: {}건, 조회: {}건",
                userId,
                result.getSubscriptions().size(),
                result.getComments().size(),
                result.getCommentLikes().size(),
                result.getArticleViews().size());

        return result;
    }

    @Override
    public UserActivityDto getUserActivityWithCache(String userId) {
        log.info("사용자 활동내역 조회 시작 (캐시): userId={}", userId);

        Optional<UserActivityCacheDocument> cached = cacheRepository.findById(userId);

        if (cached.isPresent()) {
            log.info("Cache HIT: userId={}", userId);
            return mapper.toDto(cached.get());
        }

        log.info("Cache MISS: userId={}", userId);

        UserActivityDto result = getUserActivity(userId);

        saveToCache(result);

        log.info("사용자 활동내역 조회 완료 (캐시): userId={}", userId);
        return result;
    }

    private void saveToCache(UserActivityDto dto) {
        try {
            UserActivityCacheDocument document = mapper.toDocument(dto);
            cacheRepository.save(document);
            log.info("MongoDB 캐시 저장 완료: userId={}", dto.getId());
        } catch (Exception e) {
            log.error("MongoDB 캐시 저장 실패: userId={}", dto.getId(), e);
            throw new RuntimeException("캐시 저장에 실패했습니다.", e);
        }
    }

    /**
     * Object[] Raw 데이터를 UserActivityDto로 변환
     * Object[] 구조: { userId, email, nickname, createdAt, subscriptionsJson, commentsJson, likesJson, viewsJson }
     */
    private UserActivityDto parseRawDataToDto(Object[] rawData, Long userId) {
        try {
            // User 정보 추출
            String email = (String) rawData[1];
            String nickname = (String) rawData[2];
            LocalDateTime userCreatedAt = ((Timestamp) rawData[3]).toLocalDateTime();

            // JSON 문자열 추출
            String subscriptionsJson = rawData[4].toString();
            String commentsJson = rawData[5].toString();
            String likesJson = rawData[6].toString();
            String viewsJson = rawData[7].toString();

            log.debug("JSON 파싱 시작: userId={}", userId);

            // JSON → DTO 리스트 변환
            List<SubscribesActivityDto> subscriptions = parseJsonToList(
                    subscriptionsJson,
                    new TypeReference<List<SubscribesActivityDto>>() {}
            );

            List<CommentActivityDto> comments = parseJsonToList(
                    commentsJson,
                    new TypeReference<List<CommentActivityDto>>() {}
            );

            List<CommentLikeActivityDto> likes = parseJsonToList(
                    likesJson,
                    new TypeReference<List<CommentLikeActivityDto>>() {}
            );

            List<ArticleViewActivityDto> views = parseJsonToList(
                    viewsJson,
                    new TypeReference<List<ArticleViewActivityDto>>() {}
            );

            log.debug("JSON 파싱 완료: userId={}", userId);

            // UserActivityDto 조합
            return UserActivityDto.builder()
                    .id(userId.toString())
                    .email(email)
                    .nickname(nickname)
                    .createdAt(userCreatedAt)
                    .subscriptions(subscriptions)
                    .comments(comments)
                    .commentLikes(likes)
                    .articleViews(views)
                    .build();

        } catch (Exception e) {
            log.error("Raw 데이터 파싱 실패: userId={}", userId, e);
            throw new RuntimeException("사용자 활동 데이터 파싱에 실패했습니다.", e);
        }
    }

    /**
     * JSON 문자열을 DTO 리스트로 변환
     */
    private <T> List<T> parseJsonToList(String json, TypeReference<List<T>> typeReference) {
        try {
            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            log.error("JSON 파싱 실패: json={}", json, e);
            throw new RuntimeException("JSON 파싱에 실패했습니다.", e);
        }
    }
}