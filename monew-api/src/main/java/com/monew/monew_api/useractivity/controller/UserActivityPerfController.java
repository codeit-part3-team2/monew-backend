package com.monew.monew_api.useractivity.controller;

import com.monew.monew_api.useractivity.dto.UserActivityDto;
import com.monew.monew_api.useractivity.service.UserActivityCacheService;
import com.monew.monew_api.useractivity.service.UserActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserActivityPerfController {

    private final UserActivityCacheService cacheService;
    private final UserActivityService userActivityService;

    @GetMapping("/api/test/user-activity/{userId}")
    public UserActivityDto testUserActivity(@PathVariable String userId,
                                            @RequestParam(defaultValue = "cache") String mode) {
        // mode 값: cache | single | multi
        long start = System.currentTimeMillis();

        try {
            switch (mode) {
                case "cache":
                    return cacheService.getUserActivityWithCache(userId);

                case "single":
                    return userActivityService.getUserActivitySingleQuery(userId);

                case "multi":
                    return userActivityService.getUserActivity(userId);

                default:
                    throw new IllegalArgumentException("mode 파라미터는 cache | single | multi 중 하나여야 합니다.");
            }
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            log.info("[PERF] mode={} took={} ms", mode, elapsed);
        }
    }
}
