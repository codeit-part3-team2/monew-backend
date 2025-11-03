package com.monew.monew_api.useractivity.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monew.monew_api.comments.entity.Comment;
import com.monew.monew_api.comments.entity.CommentLike;
import com.monew.monew_api.common.exception.user.UserNotFoundException;
import com.monew.monew_api.domain.user.User;
import com.monew.monew_api.domain.user.repository.UserRepository;
import com.monew.monew_api.subscribe.entity.Subscribe;
import com.monew.monew_api.useractivity.document.UserActivityCacheDocument;
import com.monew.monew_api.useractivity.dto.ArticleViewActivityDto;
import com.monew.monew_api.useractivity.dto.UserActivityDto;
import com.monew.monew_api.useractivity.mapper.UserActivityMapper;
import com.monew.monew_api.useractivity.mapper.UserActivityRawMapper;
import com.monew.monew_api.useractivity.repository.UserActivityCacheRepository;
import com.monew.monew_api.useractivity.repository.UserActivityRepository;
import com.monew.monew_api.useractivity.repository.projection.UserActivityRaw;
import com.monew.monew_api.useractivity.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final UserActivityRawMapper rawMapper;
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

        UserActivityDto result = mapper.toUserActivityDto(user, subscriptions, comments, likes, views);

        log.info("사용자 활동내역 조회 완료: userId={}", userId);
        return result;
    }

    /**
     * 추가: 단일 쿼리 방식
     */
    @Override
    @Transactional(readOnly = true)
    public UserActivityDto getUserActivitySingleQuery(String userId) {
        log.info("사용자 활동내역 조회 시작 (단일 쿼리 - Record): userId={}", userId);

        Long userIdLong = Long.parseLong(userId);

        UserActivityRaw raw = activityRepository.findUserActivityRaw(userIdLong);

        if (raw == null) {
            log.error("사용자 활동 데이터를 찾을 수 없음: userId={}", userId);
            throw new UserNotFoundException();
        }

        // 2. Record → DTO 변환
        UserActivityDto result = rawMapper.toDto(raw);

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
            log.info("사용자 활동내역 조회 완료 (캐시)");
            return mapper.toDto(cached.get());
        }

        log.info("Cache MISS: userId={}", userId);

        UserActivityDto result = getUserActivitySingleQuery(userId);

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
}