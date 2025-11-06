package com.monew.monew_api.useractivity.service.Impl;

import com.monew.monew_api.comments.entity.Comment;
import com.monew.monew_api.comments.entity.CommentLike;
import com.monew.monew_api.common.exception.user.UserNotFoundException;
import com.monew.monew_api.user.User;
import com.monew.monew_api.user.repository.UserRepository;
import com.monew.monew_api.subscribe.entity.Subscribe;
import com.monew.monew_api.useractivity.dto.ArticleViewActivityDto;
import com.monew.monew_api.useractivity.dto.UserActivityDto;
import com.monew.monew_api.useractivity.mapper.UserActivityMapper;
import com.monew.monew_api.useractivity.mapper.UserActivityRawMapper;
import com.monew.monew_api.useractivity.repository.UserActivityRepository;
import com.monew.monew_api.useractivity.repository.projection.UserActivityRaw;
import com.monew.monew_api.useractivity.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserActivityServiceImpl implements UserActivityService {

    private final UserRepository userRepository;
    private final UserActivityRepository activityRepository;
    private final UserActivityMapper mapper;
    private final UserActivityRawMapper rawMapper;

    @Override
    @Transactional(readOnly = true)
    public UserActivityDto getUserActivity(String userId) {
        log.info("[UserActivity] 사용자 활동내역 조회 시작: userId={}", userId);

        Long userIdLong = Long.parseLong(userId);

        User user = userRepository.findById(userIdLong)
                .orElseThrow(UserNotFoundException::new);

        List<Subscribe> subscriptions = activityRepository.findSubscriptionsByUserId(userIdLong);
        log.info("[UserActivity] 구독 정보 조회 완료: {}건", subscriptions.size());

        List<Comment> comments = activityRepository.findRecentCommentsByUserId(userIdLong);
        log.info("[UserActivity] 최근 댓글 조회 완료: {}건", comments.size());

        List<CommentLike> likes = activityRepository.findRecentLikesByUserId(userIdLong);
        log.info("[UserActivity] 최근 좋아요 조회 완료: {}건", likes.size());

        List<ArticleViewActivityDto> views = activityRepository.findRecentViewsByUserId(userIdLong);
        log.info("[UserActivity] 최근 조회 기사 조회 완료: {}건", views.size());

        UserActivityDto result = mapper.toUserActivityDto(user, subscriptions, comments, likes, views);

        log.info("[UserActivity] 사용자 활동내역 조회 완료: userId={}", userId);
        return result;
    }

    /**
     * 추가: 단일 쿼리 방식
     */
    @Override
    @Transactional(readOnly = true)
    public UserActivityDto getUserActivitySingleQuery(String userId) {
        log.info("[UserActivity] 사용자 활동내역 조회 시작 (단일 쿼리 - Record): userId={}", userId);

        Long userIdLong = Long.parseLong(userId);

        UserActivityRaw raw = activityRepository.findUserActivityRaw(userIdLong);

        if (raw == null) {
            log.error("[UserActivity] 사용자 활동 데이터를 찾을 수 없음: userId={}", userId);
            throw new UserNotFoundException();
        }

        UserActivityDto result = rawMapper.toDto(raw);

        log.info("[UserActivity] 사용자 활동내역 조회 완료 (단일 쿼리): userId={}, 구독: {}건, 댓글: {}건, 좋아요: {}건, 조회: {}건",
                userId,
                result.getSubscriptions().size(),
                result.getComments().size(),
                result.getCommentLikes().size(),
                result.getArticleViews().size());

        return result;
    }
}