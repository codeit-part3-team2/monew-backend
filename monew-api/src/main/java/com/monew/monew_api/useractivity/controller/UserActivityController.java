package com.monew.monew_api.useractivity.controller;

import com.monew.monew_api.useractivity.dto.UserActivityDto;
import com.monew.monew_api.useractivity.service.UserActivityCacheService;
import com.monew.monew_api.useractivity.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user-activities")
@RequiredArgsConstructor
public class UserActivityController {

    private final UserActivityService userActivityService;
    private final UserActivityCacheService userActivityCacheService;

    /*
     userActivityCacheService.
     mongoDB 사용 시 getUserActivityWithCache 메서드

     userActivityService.
     단일 쿼리 사용시 getUserActivitySingleQuery 메서드 (네이티브 쿼리)
     여러 쿼리 사용시 getUserActivity 메서드
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserActivityDto> getUserActivity(
            @PathVariable String userId,
            @RequestHeader(value = "MoNew-Request-User-ID", required = false) Long requesterId
    ) {
        log.info("[활동내역 API 요청]: userId={}", userId);

//        UserActivityDto activity = userActivityService.getUserActivitySingleQuery(userId);
        UserActivityDto activity = userActivityCacheService.getUserActivityWithCache(userId);

        log.info("[활동내역 API 응답]: userId={}, Subscriptions_size={}, Comments_size={}, CommentLikes_size={}, ArticleViews_size={}",
                activity.getId(), activity.getSubscriptions().size(), activity.getComments().size(),
                activity.getCommentLikes().size(), activity.getArticleViews().size());

        return ResponseEntity.ok(activity);
    }
}